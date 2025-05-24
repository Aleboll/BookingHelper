package Interface.Client;

public class Client {
    private int id;
    private String name;
    private String phone;
    private String email;

    public Client(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}