package com.ntros.systemdesign.mesaging.idempotency;

import com.ntros.systemdesign.mesaging.dualchannel.ChannelSettings;
import com.ntros.systemdesign.mesaging.dualchannel.DualMessageChannel;
import com.ntros.systemdesign.mesaging.idempotency.inbox.InboxPaymentConnector;
import com.ntros.systemdesign.mesaging.idempotency.inbox.InboxConnector;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.Account;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.CancellationToken;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.ExecutionContext;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.RuntimeContext;
import com.ntros.systemdesign.mesaging.idempotency.inbox.repository.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class InboxMain {

  public static void main(String[] args) throws InterruptedException {
    // initialize db
    // create accounts with random balance
    long seed = 52;
    Random rng = new Random(seed);
    initDbAccounts(500, rng, 100, 300);
    List<String> accountIds =
        PersistenceContext.database().getAllAccounts().stream()
            .map(Account::getAccountId)
            .toList();

    // initialize message channel and runtime settings
    Object dbLock = new Object();
    CancellationToken workerToken = new CancellationToken();
    int sendDelayMs = 500;
    int receiveDelayMs = 500;
    int workerCount = 2;
    var channel = new DualMessageChannel(ChannelSettings.ofSettings(10, 10));

    RuntimeContext runtimeContext = new RuntimeContext(channel, dbLock, workerCount);

    // create connector, processor starts here
    InboxConnector connector = new InboxPaymentConnector(runtimeContext);
    List<ServerResponse> processedMessages = new ArrayList<>();
    Object processedLock = new Object();

    // build workers
    List<MessageSender> senders = new ArrayList<>();
    for (int i = 1; i <= workerCount; i++) {
      senders.add(
          new MessageSender(
              i,
              new ExecutionContext(
                  connector, accountIds, rng, sendDelayMs, processedMessages, processedLock),
              workerToken));
    }
    List<MessageReceiver> receivers = new ArrayList<>();
    for (int i = 1; i <= workerCount; i++) {
      receivers.add(
          new MessageReceiver(
              i,
              new ExecutionContext(
                  connector, accountIds, rng, receiveDelayMs, processedMessages, processedLock)));
    }
    // start workers
    for (var s : senders) {
      s.start();
    }
    for (var r : receivers) {
      r.start();
    }
    // run for x seconds
    Thread.sleep(32_000);

    workerToken.cancel();
    for (var s : senders) {
      s.awaitTermination();
    }

    connector.shutdown();

    for (var r : receivers) {
      r.awaitTermination();
    }
  }

  private static void initDbAccounts(
      int accounts, Random rng, int balanceOrigin, int balanceBound) {
    for (int i = 1; i <= accounts; i++) {
      var acc =
          new Account(UUID.randomUUID().toString(), rng.nextLong(balanceOrigin, balanceBound));

      PersistenceContext.database().insertAccount(acc);
    }
  }
}
