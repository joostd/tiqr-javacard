package terminal;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import java.util.List;

public class Echo {

    static final String MSG_ERROR = "    -- error --     ";
    static final String MSG_DISABLED = " -- insert card --  ";
    static final String MSG_INVALID = " -- invalid card -- ";

    private static final long serialVersionUID = 1L;
    static final String TITLE = "Calculator";

    static final byte[] CALC_APPLET_AID = { (byte) 0x3B, (byte) 0x29,
            (byte) 0x63, (byte) 0x61, (byte) 0x6C, (byte) 0x63, (byte) 0x01 };

    static final CommandAPDU SELECT_APDU = new CommandAPDU(
    		(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, CALC_APPLET_AID);
    
    CardChannel applet;

    public Echo() {
    }

    void setText(ResponseAPDU apdu) {
        byte[] data = apdu.getData();
        int sw = apdu.getSW();
        if (sw != 0x9000 || data.length < 5) {
            System.err.println(MSG_ERROR);
        } else {
            System.err.println((short) (((data[3] & 0x000000FF) << 8) | (data[4] & 0x000000FF)));
        }
    }

    public ResponseAPDU sendKey() {
        CommandAPDU apdu = new CommandAPDU(0, 41, 0, 0, 5);
        try {
			return applet.transmit(apdu);
		} catch (CardException e) {
			return null;
		}
    }

    public static void main(String[] arg) {
        new Echo().run();
    }

    public void run() {
        try {
            TerminalFactory tf = TerminalFactory.getDefault();
            CardTerminals ct = tf.terminals();
            List<CardTerminal> cs = ct.list(CardTerminals.State.CARD_PRESENT);
            if (cs.isEmpty()) {
                System.err.println("No terminals with a card found.");
                return;
            }

            while (true) {
                System.err.print(".");
                try {
                    for(CardTerminal c : cs) {
                        if (c.isCardPresent()) {
                            try {
                                Card card = c.connect("*");
                                try {
                                    applet = card.getBasicChannel();
                                    ResponseAPDU resp = applet.transmit(SELECT_APDU);
                                    if (resp.getSW() != 0x9000) {
                                        throw new Exception("Select failed");
                                    }


                                    CommandAPDU apdu = new CommandAPDU(0, 41, 0, 0, 5);
                                    try {
                                        ResponseAPDU rsp = applet.transmit(apdu);
                                        byte[] data = rsp.getData();
                                        int sw = rsp.getSW();
                                        if (sw != 0x9000 || data.length < 5) {
                                            System.err.println(MSG_ERROR);
                                        } else {
                                            System.err.println((short) data[3]);
                                        }

                                    } catch (CardException e) {
                                    }


                                    // Wait for the card to be removed
                                    while (c.isCardPresent());
                                    System.err.println(MSG_DISABLED);
                                    break;
                                } catch (Exception e) {
                                    System.err.println("Card does not contain CalcApplet?!");
                                    System.err.println(MSG_INVALID);
                                    System.err.println(MSG_DISABLED);
                                    continue;
                                }
                            } catch (CardException e) {
                                System.err.println("Couldn't connect to card!");
                                System.err.println(MSG_INVALID);
                                System.err.println(MSG_DISABLED);
                                continue;
                            }
                        } else {
                            System.err.println("No card present!");
                            System.err.println(MSG_INVALID);
                            System.err.println(MSG_DISABLED);
                            continue;
                        }
                    }
                } catch (CardException e) {
                    System.err.println("Card status problem!");
                }
            }
        } catch (Exception e) {
            System.err.println(MSG_ERROR);
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
