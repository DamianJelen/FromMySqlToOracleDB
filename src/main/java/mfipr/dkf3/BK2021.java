package mfipr.dkf3;

import mfipr.dkf3.mySql.Run;

public class BK2021 {
    public static void main(String[] args) {
        Run.runExportDB();
        mfipr.dkf3.oracle.Run.runCreateDB();
    }
}
