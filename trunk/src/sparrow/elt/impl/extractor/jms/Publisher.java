package sparrow.elt.impl.extractor.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Publisher {

	private final Session s;

	private final Destination d;

	private final String[] props;

	private boolean isTransacted;

	private boolean ackRequired;

	/**
	 * 
	 * @param s
	 *            Session
	 * @param d
	 *            Destination
	 */
	public Publisher(Session s, Destination d, String[] props) {
		this.s = s;
		this.d = d;
		this.props = props;
		try {
			this.isTransacted = s.getTransacted();
			this.ackRequired = (s.getAcknowledgeMode() != Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException ex) {
		}
	}

	/**
	 * 
	 * @param message
	 *            Message
	 */
	public void publish(SparrowJMSMessage message, String reason) {

		MessageProducer producer = null;
		try {
			producer = s.createProducer(d);
			TextMessage m = s.createTextMessage(message.getMessage());
			m.setStringProperty("reason", reason);
			if(props!=null){
				preserveHeaderProperties(message,m);
			}
			producer.send(m);
			if (isTransacted) {
				s.commit();
			} else if (ackRequired) {
				m.acknowledge();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			if(producer!=null){
				try {
					producer.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * @param smsg
	 * @param msg
	 * @throws JMSException
	 */
	private void preserveHeaderProperties(SparrowJMSMessage smsg, Message msg)
			throws JMSException {
		for (int i = 0; i < props.length; i++) {
			msg.setStringProperty(props[i], smsg.getHeaderProperty(props[i]));
		}
	}

	/**
	 * 
	 */
	public void close() throws Exception {
		if (s != null) {
			s.close();
		}
	}

}
