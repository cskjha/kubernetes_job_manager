package com.vmware.lambda.provider.scaler.core;

public class States {


    public static class Availability {
        private String qualifier;
        private int maxCapacity;
        private int occupiedCapacity;

        public boolean hasCapacity() {
            return maxCapacity - occupiedCapacity > 0;
        }

        public String getQualifier() {
            return qualifier;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public int getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        public int getOccupiedCapacity() {
            return occupiedCapacity;
        }

        public void setOccupiedCapacity(int occupiedCapacity) {
            this.occupiedCapacity = occupiedCapacity;
        }

        @Override
        public String toString() {
            return "Availability{" +
                    "qualifier='" + qualifier + '\'' +
                    ", maxCapacity=" + maxCapacity +
                    ", occupiedCapacity=" + occupiedCapacity +
                    '}';
        }
    }

    public static class ExecutionState {
        // this could be app or function. Implementation can choose the granularity. For K8S Job Impl, this should be function. For DIND/DOOD, this should be app
        private String qualifier;
        private String status;
        private int count;
        private long asOf;

        public String getQualifier() {
            return qualifier;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public long getAsOf() {
            return asOf;
        }

        public void setAsOf(long asOf) {
            this.asOf = asOf;
        }

        @Override
        public String toString() {
            return "ExecutionState{" +
                    "qualifier='" + qualifier + '\'' +
                    ", status='" + status + '\'' +
                    ", count=" + count +
                    ", asOf=" + asOf +
                    '}';
        }
    }

    public static class Correction {
        private int parallelism, completions;
        private String qualifier;

        public int getParallelism() {
            return parallelism;
        }

        public void setParallelism(int parallelism) {
            this.parallelism = parallelism;
        }

        public int getCompletions() {
            return completions;
        }

        public void setCompletions(int completions) {
            this.completions = completions;
        }

        public String getQualifier() {
            return qualifier;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public String toString() {
            return "Correction{" +
                    "parallelism=" + parallelism +
                    ", completions=" + completions +
                    ", qualifier='" + qualifier + '\'' +
                    '}';
        }
    }
}
