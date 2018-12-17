package org.eclipse.smarthome.binding.opensensenetwork.internal;

public class test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        // System.out.println(OHItem.getLinkForMeasurand("temperature"));
        FakeLiveData();
    }

    public static void FakeLiveData() {
        int startValue = 20;
        int step = 5;
        for (int i = 0; i < 6; i++) {
            int value = startValue + i * step;
            OSPostRequest.PostValueToOSSensor(40549, value);
        }
        for (int i = 5; i >= 0; i--) {
            int value = startValue + i * step;
            OSPostRequest.PostValueToOSSensor(40549, value);
        }
        System.out.println("OK!");
    }

}
