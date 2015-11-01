import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ServerController {

  // private MessageList messages;
  private LinkList links;
  private ConnectionList connections;
  private Random rand = new Random();
  private Route route;
  private MessageList messages;

  public ServerController(MessageList messages, LinkList links,
      ConnectionList connections, Route route) {
    // this.messages = messages;
    this.connections = connections;
    this.links = links;
    this.route = route;
    this.messages = messages;
  }

  private void addLink(int from, int to, String group) {
    int nodeA = connections.get(from).getNode();
    int nodeB = connections.get(to).getNode();
    System.out.println("Create link between " + nodeA + " and " + nodeB);
    if (!links.isNeighbour(nodeA, nodeB)) {
      links.addElement(new Link(nodeA, nodeB, group));
    }
  }

  private String checkGroups(int[] selected) {
    if (selected.length < 2) {
      return null;
    }
    String group = connections.get(selected[0]).getGroup();
    for (int i = 1; i < selected.length; i++) {
      if (!group.equals(connections.get(selected[i]).getGroup())) {
        System.out.println("Links not created - more than one group selected.");
        return null;
      }
    }
    return group;
  }

  public void bind(final JList<Connection> listNodes,
      final JList<Link> listLinks, JButton btnCircular, JButton btnDeleteLink,
      final JLabel labelDrop, final JSlider sliderFailure,
      final JSpinner spinDelay, JButton btnCreateLink,
      final JCheckBox chckbxWhoisOnly, final JLabel labelCorruption,
      final JSlider sliderCorruption, final JButton btnNextStage,
      final JSpinner spinOffset, final JLabel serverStatus) {

    btnCreateLink.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int[] selected = listNodes.getSelectedIndices();
        String group = checkGroups(selected);
        if (group != null) {
          addLink(selected[0], selected[1], group);
        }
      }
    });

    btnCircular.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        int[] selected = listNodes.getSelectedIndices();
        String group = checkGroups(selected);
        if (group != null) {
          // Shuffle the list of nodes.
          for (int index = selected.length - 1; index > 0; index--) {
            int other = rand.nextInt(index);
            int temp = selected[other];
            selected[other] = selected[index];
            selected[index] = temp;
          }
          for (int i = 0; i < selected.length - 1; i++) {
            addLink(selected[i], selected[i + 1], group);
          }
          if (selected.length > 2) {
            addLink(selected[selected.length - 1], selected[0], group);
          }
        }
      }
    });

    btnDeleteLink.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Delete link");
        int[] selected = listLinks.getSelectedIndices();
        for (int i = selected.length - 1; i >= 0; i--) {
          links.remove(selected[i]);
        }
      }
    });

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
        Map<String, Network> cycles = new HashMap<String, Network>();
        if (links.nextHasMessages()) {
          for (String group : findGroups()) {
            Network groupCycle = new Network(links, group, connections);
            if (!groupCycle.hamiltonian()) {
              JOptionPane.showMessageDialog(null, "The " + group
                  + " group has no (complete) cycle so message recipients cannot be calculated.\n"
                  + "Please create a cycle for this network in order to move to one of the message sending tasks (offset <> 0)");
              return;
            }
            cycles.put(group, groupCycle);
          }
        }

        // clear message queue and send new status messages
        System.out.println("Send Status Out");
        links.nextStage(); // move on all indicators.
        Formatter f = new Formatter();
        f.format("<html><head><style type='text/css'>");
        f.format("body { color: #4444ff; font-weight: bold;}");
        f.format("table { border-collapse: collapse;}");
        f.format("span.value {color: black; }");
        f.format(
            "table td { padding-left:6mm; padding-right: 6mm; border: 1px solid black; text-align: center}");
        String serverStatusLike = "<table><tr><td>#msgs( <span class='value'>%04d</span> )</td><td>delay( <span class='value'>%02d</span> )</td><td> drop( <span class='value'>%02d%%</span> ) </td><td> corruption( <span class='value'>%02d%%</span> )</td><td>offset( <span class='value'>%02d</span> ) </td><td> whois( <span class='value'>%.1B</span> )</td></tr></table></html>";
        f.format(serverStatusLike, messages.size(), links.getDelay(),
            links.getDropRate(), links.getCorruptionRate(), links.getOffset(),
            links.isCheckwhois());
        serverStatus.setText(f.toString());
        f.close();
        route.updateStatus(cycles);
      }
    });

  }

  private Set<String> findGroups() {
    Set<String> retval = new HashSet<String>();
    int l = connections.size();
    for (int i = 0; i < l; i++) {
      retval.add(connections.get(i).getGroup());
    }
    return retval;
  }

}
