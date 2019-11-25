package util.client;

public enum  ClientHolder {
    INSTANCE;

    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
