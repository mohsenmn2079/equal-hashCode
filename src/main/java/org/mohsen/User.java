package org.mohsen;


public class User {
    int id;
    String name;

    public User(String name) {
        this.name = name;
    }
    public void printName() {
        System.out.println(name);
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).name.equals(name);
    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
