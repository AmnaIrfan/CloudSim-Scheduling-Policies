package org.cloudsim.extensions;

public enum CloudletPriorityLevel {
        LOW(0),
        NORMAL(1),
        IMPORTANT(2),
        CRITICAL(3);

        private final int value;
        private CloudletPriorityLevel(int value) {
                this.value = value;
        }

        public int getValue() {
                return value;
        }
}
