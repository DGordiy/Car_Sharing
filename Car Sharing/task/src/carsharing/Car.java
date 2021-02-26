package carsharing;

public class Car implements Entity {
    private int id;
    private int companyId;
    private String name;

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Car(int id, String name, int companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }

    public Car(String name, int companyId) {
        this.name = name;
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return String.format("%d. %s", id, name);
    }
}
