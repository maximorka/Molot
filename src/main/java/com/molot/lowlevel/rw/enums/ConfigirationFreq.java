package com.molot.lowlevel.rw.enums;

/**
 * Created by integer on 3/12/20.
 */

public enum ConfigirationFreq {
            conf0123(27, "0 1 2 3"),
            conf0132(30, "0 1 3 2"),
            conf0213(39, "0 2 1 3"),
            conf0231(45, "0 2 3 1"),
            conf0321(57, "0 3 2 1"),
            conf0312(54, "0 3 1 2"),
            conf1023(75, "1 0 2 3"),
            conf1032(78, "1 0 3 2"),
            conf1203(99, "1 2 0 3"),
            conf1230(108, "1 2 3 0"),
            conf1302(114, "1 3 0 2"),
            conf1320(120, "1 3 2 0"),
            conf2013(135, "2 0 1 3"),
            conf2031(141, "2 0 3 1"),
            conf2130(156, "2 1 3 0"),
            conf2103(147, "2 1 0 3"),
            conf2310(180, "2 3 1 0"),
            conf2301(177, "2 3 0 1"),
            conf3012(198, "3 0 1 2"),
            conf3021(201, "3 0 2 1"),
            conf3120(216, "3 1 2 0"),
            conf3102(210, "3 1 0 2"),
            conf3210(228, "3 2 1 0"),
            conf3201(225, "3 2 0 1");

            ConfigirationFreq(int conf, String description){
                this.conf = conf;
                this.description = description;

            }

            private int conf;
            private String description;

            public int getConf() { return conf;}

            @Override
            public String toString(){ return description;}
}
