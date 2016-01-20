package netsim;

public enum TaskType {
    TOPOLOGY("Topology"), SEND("Message"), WHOIS("WhoIs");

    String rep;
    TaskType(String rep) {
        this.rep = rep;
    }
    
    public String toString() {
        return rep;
    }
}
