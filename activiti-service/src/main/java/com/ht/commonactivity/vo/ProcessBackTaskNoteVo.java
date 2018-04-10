package com.ht.commonactivity.vo;

public class ProcessBackTaskNoteVo {
    String id;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessBackTaskNoteVo person = (ProcessBackTaskNoteVo) o;

        if (!id.equals(person.id)) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
//        result = 31 * result + name.hashCode();
        return result;
    }
}
