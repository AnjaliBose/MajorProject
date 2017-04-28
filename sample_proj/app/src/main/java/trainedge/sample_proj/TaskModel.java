package trainedge.sample_proj;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by CISE on 03/04/2017.
 */

class TaskModel {
    private final String key;
    String uid;
     String taskName;
    String address;
    double lat;
    double lng;
    float radius;
    String chosenRingtone;
    String dateStr;
    boolean status;

    public TaskModel(DataSnapshot dataSnapshot) {
        this.taskName = dataSnapshot.child("name").getValue(String.class);
        this.address = dataSnapshot.child("address").getValue(String.class);
        key = dataSnapshot.getKey();
    }

    public String getKey() {
        return key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getChosenRingtone() {
        return chosenRingtone;
    }

    public void setChosenRingtone(String chosenRingtone) {
        this.chosenRingtone = chosenRingtone;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
