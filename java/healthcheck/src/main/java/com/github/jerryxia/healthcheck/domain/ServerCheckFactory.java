/**
 * 
 */
package com.github.jerryxia.healthcheck.domain;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.jerryxia.healthcheck.domain.conf.ServerNode;
import com.github.jerryxia.healthcheck.util.RecordLogViewStatusMessagesServlet;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Administrator
 *
 */
@Slf4j
public class ServerCheckFactory {
    public static final HashMap<String, ServerCheckManager> MANAGERS = new HashMap<String, ServerCheckManager>();

    public static void dispatch(ArrayList<ServerNode> serverNodes) {
        if (serverNodes.size() > 0) {
            log.info("dispatch new ServerCheckManager");
            val serverNodesIterator = serverNodes.iterator();
            while (serverNodesIterator.hasNext()) {
                ServerNode serverNode = serverNodesIterator.next();
                if (serverNode != null) {
                    ServerCheckManager serverCheckManager = MANAGERS.get(serverNode.getServerName());
                    if (serverCheckManager == null) {
                        try {
                            serverCheckManager = new ServerCheckManager(serverNode);
                        } catch (Exception e) {
                            RecordLogViewStatusMessagesServlet.error(e.getMessage(), ServerCheckFactory.class);
                        }
                        MANAGERS.put(serverNode.getServerName(), serverCheckManager);
                    } else {
                        serverCheckManager.stop();
                    }
                }
            }
            val managersIterator = MANAGERS.entrySet().iterator();
            while (managersIterator.hasNext()) {
                val entry = managersIterator.next();
                entry.getValue().start();
            }
            log.info("dispatch new ServerCheckManager end");
        } else {
            log.info("iterator all exist ServerCheckManager, to stop.");
            val managersIterator = MANAGERS.entrySet().iterator();
            while (managersIterator.hasNext()) {
                val entry = managersIterator.next();
                entry.getValue().stop();
            }
            log.info("stop all exist ServerCheckManager");
        }
    }

}
