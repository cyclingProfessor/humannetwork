import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Formatter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ServerController {

    // private MessageList messages;
    private LinkList links;
    private Route route;
    private PacketList messages;
    private ConnectionList connections;

    public ServerController(PacketList messages, LinkList links,
            ConnectionList connections, Route route) {
        this.links = links;
        this.route = route;
        this.messages = messages;
        this.connections = connections;
    }

    public void bind(final JList<Connection> listNodes,
            final JList<Link> listLinks, final JLabel labelDrop,
            final JSlider sliderFailure, final JSpinner spinDelay,
            final JCheckBox chckbxWhoisOnly, final JLabel labelCorruption,
            final JSlider sliderCorruption, final JButton btnNextStage,
            final JSpinner spinOffset, final JLabel serverStatus) {

        sliderFailure.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                int val = sliderFailure.getValue();
                labelDrop.setText(val + "%");
                links.setDropRate(val);
            }
        });

        spinDelay.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                int val = (Integer) spinDelay.getValue();
                links.setDelay(val);
            }
        });

        spinOffset.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                int val = (Integer) spinOffset.getValue();
                links.setOffset(val);
            }
        });

        chckbxWhoisOnly.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                boolean b = chckbxWhoisOnly.isSelected();
                links.setCheckwhois(b);
            }
        });

        sliderCorruption.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                int val = (Integer) sliderCorruption.getValue();
                links.setCorruptionRate(val);
                labelCorruption.setText(val + "%");
            }
        });

        btnNextStage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // clear message queue and send new status messages
                System.out.println("Send Status Out");
                links.nextStage(); // move on all indicators.
                Formatter f = new Formatter();
                f.format("<html><head><style type='text/css'>");
                f.format("body { color: #4444ff; font-weight: bold;}");
                f.format("table { border-collapse: collapse;}");
                f.format("span.value {color: black; }");
                f.format("table td { padding-left:6mm; padding-right: 6mm; border: 1px solid black; text-align: center}");
                String serverStatusLike = "<table><tr><td>#msgs( <span class='value'>%04d</span> )</td><td>delay( <span class='value'>%02d</span> )</td><td> drop( <span class='value'>%02d%%</span> ) </td><td> corruption( <span class='value'>%02d%%</span> )</td><td>offset( <span class='value'>%02d</span> ) </td><td> whois( <span class='value'>%.1B</span> )</td></tr></table></html>";
                f.format(serverStatusLike, messages.size(), links.getDelay(),
                        links.getDropRate(), links.getCorruptionRate(),
                        links.getOffset(), links.isCheckwhois());
                serverStatus.setText(f.toString());
                f.close();
                route.updateStatus();
            }
        });

    }
}
