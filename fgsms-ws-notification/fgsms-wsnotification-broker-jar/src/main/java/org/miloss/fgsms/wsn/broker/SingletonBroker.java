/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.miloss.fgsms.wsn.broker;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.*;
import org.oasis_open.docs.wsn.b_2.*;
import org.oasis_open.docs.wsn.b_2.CreatePullPoint;
import org.oasis_open.docs.wsn.brw_2.*;
import org.oasis_open.docs.wsrf.r_2.ResourceUnknownFaultType;
import org.miloss.fgsms.wsn.WSNUtility;

/**
 * This is a singleton message broker for the OASIS specification
 * WS-Notification No authorization is peformed in this class, authorization
 * should be performed by its callers
 *
 * @author AO
 */
public class SingletonBroker {

    private SingletonBroker() {
        
    }

    public static SingletonBroker getInstance() {
        return SingletonBrokerHolder.INSTANCE;
    }
    final static Logger log = Logger.getLogger("WS-NotificationBroker");

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    private static long messagesin = 0;
    private static long messagesout = 0;

    public static long getMessagesin() {
        return messagesin;
    }

    public static long getMessagesout() {
        return messagesout;
    }

    private static class SingletonBrokerHolder {

        private static final SingletonBroker INSTANCE = new SingletonBroker();
    }
    private static Map currentSubscriptions = new HashMap();
    private static Map currentMailboxes = new HashMap();
    /**
     * urn:wsn:mailbox
     */
    public static final String MAILBOX_URL_PREFIX = "urn:wsn:mailbox";
    

    static String GetTopicList(Notify n) {
        String s = new String();
        if (n == null) {
            return s;
        }
        for (int i = 0; i < n.getNotificationMessage().size(); i++) {
            if (n.getNotificationMessage().get(i).getTopic() != null) {
                if (n.getNotificationMessage().get(i).getTopic().getContent() != null) {
                    for (int k = 0; k < n.getNotificationMessage().get(i).getTopic().getContent().size(); k++) {
                        s = s + " " + n.getNotificationMessage().get(i).getTopic().getContent().get(k).toString();
                    }
                }
            }
        }
        return s.trim();
    }

    protected static void Dispatch(Notify notify) {
        messagesin++;
        //TODO run this in another thread
        //get list of subscriber endpoints that are subscribed to this topic
        List<SubscriptionInfo> clients = GetMatchingSubscribers(notify.getNotificationMessage());
        log.log(Level.INFO, GetTopicList(notify));
        log.log(Level.INFO, "Dispatching WSN-Notify, msg count:" + notify.getNotificationMessage().size() + " to " + clients.size() + " subscribers");
        NotificationBroker port = null;
        BindingProvider bp = null;
        Map<String, Object> ctx = null;

        
            NotificationService client = new  NotificationService();//wsdllocation,  org.miloss.fgsms.wsn.client.NotificationService.qname);
            port = client.getNotificationPort();
            bp = (BindingProvider) port;
            ctx = bp.getRequestContext();
        

        for (int i = 0; i < clients.size(); i++) {
            boolean ok = false;
            //dispatch to web service subscribers
            if (!clients.get(i).callback.startsWith(MAILBOX_URL_PREFIX)) {
                ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, clients.get(i).callback);
                for (int k = 0; k < 2; k++) {
                    try {
                        port.notify(notify);
                        int code = (Integer) bp.getResponseContext().get("javax.xml.ws.http.response.code");
                        if (code >= 200 && code < 300) {
                            ok = true;
                            messagesout++;
                            break;
                        }
                    } catch (Exception ex) {
                        log.log(Level.WARN, "could not deliver wsn notify to " + clients.get(i).callback + " " + ex.getMessage());
                        log.log(Level.DEBUG, "could not deliver wsn notify to " + clients.get(i).callback, ex);

                    }
                    if (!ok) {
                        log.log(Level.WARN, "dropping subscription " + clients.get(i).subid + " retry count exceeded");
                        currentSubscriptions.remove(clients.get(i).subid);
                    }
                }

            } else {
                //dispatch to mailboxes
                Mailbox box = (Mailbox) currentMailboxes.get(clients.get(i).callback);
                if (box != null) {
                    box.messages.add(notify);
                }
                //else                    log.log(Level.WARN, "cannot deliver message")
            }

        }
        log.log(Level.INFO, "Stats " + messagesin + " in, " + messagesout + " out");
    }

    /*
     * key subscription id key topicname
     *
     * topic.category.taxonomy.xyz.etc anyone with topics matching topic or
     * topic.category or topic.category.taxonomy if notify.topic.startswith
     * (subscription.topic) //match
     */
    private static List<SubscriptionInfo> GetMatchingSubscribers(List<NotificationMessageHolderType> notificationMessage) {
        ArrayList<SubscriptionInfo> list = new ArrayList<SubscriptionInfo>();
        if (notificationMessage.isEmpty()) {
            return list;
        }
        Map subs = new HashMap(currentSubscriptions);
        Iterator it = subs.keySet().iterator();
        while (it.hasNext()) { //for each subscription
            SubscriptionInfo si = (SubscriptionInfo) subs.get(it.next());
            //if one of the inbound notify's topic matching a subscription's topic, add to the list
            for (int i = 0; i < notificationMessage.size(); i++) {
                if (SubscriptionContains(si, notificationMessage.get(i).getTopic())) {
                    list.add(si);
                }

            }
        }
        return list;
    }

    private static boolean SubscriptionContains(SubscriptionInfo si, TopicExpressionType topic) {
        for (int i = 0; i < si.topics.size(); i++) {
            List<String> topics = WSNUtility.topicExpressionToList(topic);
            for (int k = 0; k < topics.size(); k++) {
                if (si.topics.get(i).toLowerCase().startsWith(topics.get(k).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static String AddSubscription(Subscribe subscribeRequest) {
        String s = UUID.randomUUID().toString();
        SubscriptionInfo info = new SubscriptionInfo();
        info.data = subscribeRequest;
        info.callback = WSNUtility.getWSAAddress(subscribeRequest.getConsumerReference());
        info.subid = s;
        info.topics = new ArrayList<String>();
        for (int i = 0; i < subscribeRequest.getFilter().getAny().size(); i++) {
            if ((subscribeRequest.getFilter().getAny().get(i) instanceof JAXBElement)) {
                JAXBElement<TopicExpressionType> te = (JAXBElement<TopicExpressionType>) subscribeRequest.getFilter().getAny().get(i);
                info.topics = WSNUtility.topicExpressionToList(te.getValue());
            }
        }
        currentSubscriptions.put(s, info);
        log.log(Level.INFO, "adding subscription " + info.subid + " topics " + WSNUtility.listStringtoString(info.topics) + " to address " + info.callback);

        return s;
    }

    protected static void RemoveSubscription(List<Object> any) {
        for (int i = 0; i < any.size(); i++) {
            log.log(Level.INFO, "attempting to remove subscription " + any.get(i).toString());
            if (any.get(i) instanceof String) {
                if(RemoveSubscription((String) any.get(i)))
                    log.log(Level.INFO, "successfully removed " + any.get(i).toString());
                else log.log(Level.WARN, "unsuccessful removal " + any.get(i).toString() + " it probably didn't exist");
            }
        }
    }

    protected static boolean RemoveSubscription(String subscriptionId) {
        Object remove = currentSubscriptions.remove(subscriptionId);
        if (remove == null) {
            return false;
        }
        return true;
    }

    protected static String CreateMailBox(CreatePullPoint createPullPointRequest, String username) {
        if (createPullPointRequest == null) {
            return null;
        }
        Mailbox box = new Mailbox();
        box.messages = new ConcurrentLinkedQueue<Notify>();
        box.username = username;
        box.id = MAILBOX_URL_PREFIX + UUID.randomUUID().toString();
        currentMailboxes.put(box.id, box);
        return box.id;
    }

    protected static void DestroyMailBox(List<Object> any) {
        for (int i = 0; i < any.size(); i++) {
            if (any.get(i) instanceof String) {
                Object remove = currentMailboxes.remove((String) any.get(i));
                if (remove == null)
                    ; //maybe throw resource unknown fault?
                remove = null;
            }
        }
    }

    protected static GetMessagesResponse GetMessages(GetMessages messagesRequest) throws ResourceUnknownFault, UnableToGetMessagesFault {
        if (messagesRequest == null) {
            throw new UnableToGetMessagesFault("null request", new UnableToGetMessagesFaultType());
        }
        if (messagesRequest.getAny().isEmpty()) {
            throw new UnableToGetMessagesFault("at least one pull point mailbox must be specified", new UnableToGetMessagesFaultType());
        }
        GetMessagesResponse res = new GetMessagesResponse();
        int count = 0;
        long maxmessages = 100;
        if (messagesRequest.getMaximumNumber() != null) {
            maxmessages = messagesRequest.getMaximumNumber().longValue();
        }
        if (maxmessages <= 0) {
            maxmessages = 10;
        }
        if (maxmessages > 1000) {
            maxmessages = 1000;
        }

        for (int i = 0; i < messagesRequest.getAny().size(); i++) {
            Mailbox box = (Mailbox) currentMailboxes.get((String) messagesRequest.getAny().get(i));
            if (box == null) {
                throw new ResourceUnknownFault("unknown pull point", new ResourceUnknownFaultType());
            }
            while (count < maxmessages && !box.messages.isEmpty()) {
                Notify poll = box.messages.poll();
                if (poll != null) {
                    res.getNotificationMessage().addAll(poll.getNotificationMessage());
                    count++;
                }
            }
        }
        return res;
    }

    protected static void PauseSubscription(PauseSubscription pauseSubscriptionRequest) throws PauseFailedFault, ResourceUnknownFault {

        for (int i = 0; i < pauseSubscriptionRequest.getAny().size(); i++) {
            if (pauseSubscriptionRequest.getAny().get(i) instanceof String) {
                SubscriptionInfo get = (SubscriptionInfo) currentSubscriptions.get((String) pauseSubscriptionRequest.getAny().get(i));
                if (get == null) {
                    throw new ResourceUnknownFault("subscription does not exist", new ResourceUnknownFaultType());
                }
                get.paused = true;
                log.log(Level.INFO, "Subscription id " + get.subid + " to callback " + get.callback + " paused");
            }
        }
    }

    protected static void ResumeSubscription(ResumeSubscription resumeSubscriptionRequest) throws ResumeFailedFault, ResourceUnknownFault {
        for (int i = 0; i < resumeSubscriptionRequest.getAny().size(); i++) {
            if (resumeSubscriptionRequest.getAny().get(i) instanceof String) {
                SubscriptionInfo get = (SubscriptionInfo) currentSubscriptions.get((String) resumeSubscriptionRequest.getAny().get(i));
                if (get == null) {
                    throw new ResourceUnknownFault("subscription does not exist", new ResourceUnknownFaultType());
                }
                get.paused = false;
                log.log(Level.INFO, "Subscription id " + get.subid + " to callback " + get.callback + " resumed");
            }
        }
    }
}
