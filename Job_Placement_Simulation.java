import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
class Result {
    public int totalTime;
    public double averageWaitTime;
    public double averageWasteCapacity;
    public double averageFreePMsPercentage = -1;
    public double avergaeNumberOfMigrations = -1;
    public int totalTimeForCloud = -1;
    public int totalPM_On_Cloud = -1;
    public int local_migrations_from_cloud_to_local = -1;
    public int total_Migrations_In_Cloud = -1;
}
class Job {
    public int job_id;
    public int type;
    public int dataset_size;
    public int pin1_time;
    public int pin2_time;
    public int pin3_time;
    public int pin4_time;
    public int isknapscakFlag;
    public int pin1_ideal_time;
    public int pin2_ideal_time;
    public int pin3_ideal_time;
    public int pin4_ideal_time;
    public double overhead2;
    public double overhead3;
    public double overhead4;
    public int deleta12;
    public int deleta13;
    public int deleta14;
    public int deleta23;
    public int deleta24;
    public int deleta34;
    public int allocated_pins;
    public int executionTime;
    public int start_time;
    public int end_time;
    public boolean isComplete;
    public int TimePerCore;
    public int PM;
    public int cluster_id;
    public boolean is_On_Cloud = false;
    int KPM;
    public Job(int id, int t, int size, int p1, int p2, int p3, int p4, int cluster_id) {
        job_id = id;
        type = t;
        dataset_size = size;
        pin1_time = p1;
        pin2_time = p2;
        pin3_time = p3;
        pin4_time = p4;
        isknapscakFlag = 0;
        deleta12 = 100 - (int) Math.round((double) p2 / (double) p1 * 100.0);
        deleta13 = 100 - (int) Math.round((double) p3 / (double) p1 * 100.0);
        deleta14 = 100 - (int) Math.round((double) p4 / (double) p1 * 100.0);
        deleta23 = 100 - (int) Math.round((double) p3 / (double) p2 * 100.0);
        deleta24 = 100 - (int) Math.round((double) p4 / (double) p2 * 100.0);
        deleta34 = 100 - (int) Math.round((double) p4 / (double) p3 * 100.0);
        pin1_ideal_time = p1 / 1;
        pin2_ideal_time = p1 / 2;
        pin3_ideal_time = p1 / 3;
        pin4_ideal_time = p1 / 4;
        overhead2 = (double) pin2_time / (double) pin2_ideal_time;
        overhead3 = (double) pin3_time / (double) pin3_ideal_time;
        overhead4 = (double) pin4_time / (double) pin4_ideal_time;
        isComplete = false;
        executionTime = -1;
        start_time = -1;
        end_time = -1;
        allocated_pins = -1;
        PM = -1;
        this.cluster_id = cluster_id;
        is_On_Cloud = false;
    }
    public Job(Job x) {
        this.PM = x.PM;
        this.TimePerCore = x.TimePerCore;
        this.allocated_pins = x.allocated_pins;
        this.cluster_id = x.cluster_id;
        this.dataset_size = x.dataset_size;
        this.deleta12 = x.deleta12;
        this.deleta13 = x.deleta13;
        this.deleta14 = x.deleta14;
        this.deleta23 = x.deleta23;
        this.deleta24 = x.deleta24;
        this.deleta34 = x.deleta34;
        this.end_time = x.end_time;
        this.executionTime = x.executionTime;
        this.isComplete = x.isComplete;
        this.is_On_Cloud = x.is_On_Cloud;
        this.isknapscakFlag = this.isknapscakFlag;
        this.job_id = x.job_id;
        this.overhead2 = x.overhead2;
        this.overhead3 = x.overhead3;
        this.overhead4 = x.overhead4;
        this.pin1_ideal_time = x.pin1_ideal_time;
        this.pin1_time = x.pin1_time;
        this.pin1_ideal_time = x.pin2_ideal_time;
        this.pin2_time = x.pin2_time;
        this.pin3_ideal_time = x.pin3_ideal_time;
        this.pin3_time = x.pin3_time;
        this.pin4_ideal_time = x.pin4_ideal_time;
        this.pin4_time = x.pin4_time;
        this.start_time = x.start_time;
        this.type = x.type;
    }
    public void reset_job() {
        isComplete = false;
        start_time = -1;
        end_time = -1;
        //allocated_pins = -1;
        //executionTime = -1;
        isknapscakFlag = 0;
        PM = -1;
        is_On_Cloud = false;
    }
}
class SortJobOnExecctionTime implements Comparator<Job> {
    @Override
    public int compare(Job o1, Job o2) {

        return o1.executionTime - o2.executionTime;
    }
}
class SortJobOnID implements Comparator<Job> {
    @Override
    public int compare(Job o1, Job o2) {

        return o1.job_id - o2.job_id;
    }
}
class SortJobOnPin implements Comparator<Job> {
    @Override
    public int compare(Job o1, Job o2) {

        return o1.allocated_pins - o2.allocated_pins;
    }
}
class SortByClusterId implements Comparator<Job> {
    public int compare(Job a, Job b) {
        return a.cluster_id - b.cluster_id;
    }
}
class SortByClusterExecutionTime implements Comparator<Cluster> {
   public int compare(Cluster a, Cluster b) {
        return a.total_execution_time - b.total_execution_time;
    }
}
class SortByClusterJobCount implements Comparator<Cluster> {
   public int compare(Cluster a, Cluster b) {
        return a.total_number_of_jobs - b.total_number_of_jobs;
    }
}
class PhysicalMachine implements Comparable {
    int pm_id;
    public int max_capacity;
    public int used_capacity;
    public ArrayList<Job> current_jobs;
    public PhysicalMachine(int id, int c) {
        max_capacity = c;
        used_capacity = 0;
        pm_id = id;
        current_jobs = new ArrayList<Job>();
    }   
    public PhysicalMachine(PhysicalMachine p) {
        max_capacity = p.max_capacity;
        used_capacity = p.used_capacity;
        pm_id = p.pm_id;
        current_jobs = ContainerPinsSelection.copyJobsArrayList(p.current_jobs);
    }
    public void reset_pm() {
        used_capacity = 0;
        current_jobs.clear();
    }
    @Override
    public int compareTo(Object comparePM) {
        int compareFree = ((PhysicalMachine) comparePM).max_capacity - ((PhysicalMachine) comparePM).used_capacity;
        int currentFree = max_capacity - used_capacity;
        return currentFree - compareFree;
    }
}
class Job_Generator {
    public ArrayList<Job> jobs = new ArrayList<Job>();
    public ArrayList<Job> jobs_1 = new ArrayList<Job>();
    public ArrayList<Job> jobs_2 = new ArrayList<Job>();
    public ArrayList<Job> jobs_3 = new ArrayList<Job>();
    public ArrayList<Job> jobs_4 = new ArrayList<Job>();
    public ArrayList<Job> jobs_p = new ArrayList<Job>();
    public Job_Generator() {
    }
    public Job_Generator(int numberOfJobs) throws IOException {
        int[] size = {1000, 1500, 2000, 5000, 10000, 15000, 20000, 25000, 30000, 40000, 50000, 60000, 120000};
        int[] type = {1, 2, 3};
        for (int i = 0; i < numberOfJobs; i++) {
            Random rand = new Random();
            int s = rand.nextInt(13);
            int t = rand.nextInt(3);
            Process p1 = Runtime.getRuntime().exec("python kerasLoaded.py " + type[t] + " " + size[s] + " " + 1);// 2 60000 4");//+" "+arr.get(i+4)+" ");
            BufferedReader in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            int time1 = (int) Math.ceil(Double.parseDouble(in1.readLine()));
            Process p2 = Runtime.getRuntime().exec("python kerasLoaded.py " + type[t] + " " + size[s] + " " + 2);// 2 60000 4");//+" "+arr.get(i+4)+" ");
            BufferedReader in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
            int time2 = (int) Math.ceil(Double.parseDouble(in2.readLine()));
            Process p3 = Runtime.getRuntime().exec("python kerasLoaded.py " + type[t] + " " + size[s] + " " + 3);// 2 60000 4");//+" "+arr.get(i+4)+" ");
            BufferedReader in3 = new BufferedReader(new InputStreamReader(p3.getInputStream()));
            int time3 = (int) Math.ceil(Double.parseDouble(in3.readLine()));
            Process p4 = Runtime.getRuntime().exec("python kerasLoaded.py " + type[t] + " " + size[s] + " " + 4);// 2 60000 4");//+" "+arr.get(i+4)+" ");
            BufferedReader in4 = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            int time4 = (int) Math.ceil(Double.parseDouble(in4.readLine()));
            Job j = new Job(i, type[t], size[s], time1, time2, time3, time4, -1);
            jobs.add(j);
        }
    }
    public Job_Generator(String fileName) throws IOException {
        BufferedReader as = new BufferedReader(new FileReader(fileName));
        String line = as.readLine();
        int i = 0;
        while (line != null) {
            int size = Integer.parseInt(line.split(",")[1]);
            int type = Integer.parseInt(line.split(",")[0]);
            Process p1 = Runtime.getRuntime().exec("python kerasLoaded.py " + type + " " + size + " " + 1);// 2 60000 4");//+" "+arr.get(i+4)+" ");
            BufferedReader in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            int time1 = (int) Math.ceil(Double.parseDouble(in1.readLine()));
            Process p2 = Runtime.getRuntime().exec("python kerasLoaded.py " + type + " " + size + " " + 2);// 2 60000 4");//+" "+arr.get(i+4)+" ");
            BufferedReader in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
            int time2 = (int) Math.ceil(Double.parseDouble(in2.readLine()));
            Process p3 = Runtime.getRuntime().exec("python kerasLoaded.py " + type + " " + size + " " + 3);// 2 60000 4");//+" "+arr.get(i+4)+" ");
            BufferedReader in3 = new BufferedReader(new InputStreamReader(p3.getInputStream()));
            int time3 = (int) Math.ceil(Double.parseDouble(in3.readLine()));
            Process p4 = Runtime.getRuntime().exec("python kerasLoaded.py " + type + " " + size + " " + 4);// 2 60000 4");//+" "+arr.get(i+4)+" ");
            BufferedReader in4 = new BufferedReader(new InputStreamReader(p4.getInputStream()));
            int time4 = (int) Math.ceil(Double.parseDouble(in4.readLine()));
            Job j = new Job(i, type, size, time1, time2, time3, time4, -1);
            jobs.add(j);
            i++;
            line = as.readLine();
        }
    }
    public Job_Generator(String fileName, boolean flag) throws IOException {
        if(ContainerPinsSelection.isOffloading == true)
        {
            BufferedReader as = new BufferedReader(new FileReader(fileName));
            String line = as.readLine();
            int i = 0;
            while (line != null) {
                int type = ContainerPinsSelection.tuple_Id(line.split(",")[0]);
                int size = Integer.parseInt(line.split(",")[1]);
                int time4 = Integer.parseInt(line.split(",")[2]);
                int time3 = Integer.parseInt(line.split(",")[2]);
                int time2 = Integer.parseInt(line.split(",")[2]);
                int time1 = Integer.parseInt(line.split(",")[2]);
                int pins = Integer.parseInt(line.split(",")[3]);
                Job job = new Job(i, type, size, time1, time2, time3, time4, -1);
                job.allocated_pins = pins;
                job.executionTime = job.pin1_time;
                jobs.add(job);
                i++;
                line = as.readLine();
            }
            as.close();
            return;
        }
        BufferedReader as = new BufferedReader(new FileReader(fileName));
        String line = as.readLine();
        int i = 0;
        while (line != null) {
            int size = Integer.parseInt(line.split(",")[1]);
            int type = Integer.parseInt(line.split(",")[0]);
            int time1 = Integer.parseInt(line.split(",")[5]);
            int time2 = Integer.parseInt(line.split(",")[4]);
            int time3 = Integer.parseInt(line.split(",")[3]);
            int time4 = Integer.parseInt(line.split(",")[2]);
            Job j = new Job(i, type, size, time1, time2, time3, time4, -1);
            jobs.add(j);
            i++;
            line = as.readLine();
        }
        as.close();
    }
    public Job_Generator(Job_Generator job_gen) {
        for (int i = 0; i < job_gen.jobs.size(); i++) {
            jobs.add(job_gen.jobs.get(i));
        }
    }
    public void reset_jobs() {
        reset_jobs(0);
    }
    public void reset_jobs(int pins) {
        ArrayList<Job> current_job;
        if (pins == 0) {
            current_job = jobs;
        } else if (pins == 1) {
            current_job = jobs_1;
        } else if (pins == 2) {
            current_job = jobs_2;
        } else if (pins == 3) {
            current_job = jobs_3;
        } else if (pins == 4) {
            current_job = jobs_4;
        } else {
            current_job = jobs_p;
        }
        for (int j = 0; j < current_job.size(); j++) {
            Job job = current_job.get(j);
            job.reset_job();
            current_job.remove(j);
            current_job.add(j, job);
        }
        if (ContainerPinsSelection.clustering == true) {
            current_job = ContainerPinsSelection.sort_Jobs_AccordingToCluster_Ascending(current_job);
        }
        if (pins == 0) {
            jobs = current_job;
        } else if (pins == 1) {
            jobs_1 = current_job;
        } else if (pins == 2) {
            jobs_2 = current_job;
        } else if (pins == 3) {
            jobs_3 = current_job;
        } else if (pins == 4) {
            jobs_4 = current_job;
        } else {
            jobs_p = current_job;
        }
    }
    public void SortOnExecutionTime(int pins) {
        if (pins == 0) {
            jobs.sort(new SortJobOnExecctionTime());
        }
        if (pins == 1) {
            jobs_1.sort(new SortJobOnExecctionTime());
        }
        if (pins == 2) {
            jobs_2.sort(new SortJobOnExecctionTime());
        }
        if (pins == 3) {
            jobs_3.sort(new SortJobOnExecctionTime());
        }
        if (pins == 4) {
            jobs_4.sort(new SortJobOnExecctionTime());
        }
        if (pins == -1) {
            jobs_p.sort(new SortJobOnExecctionTime());
        }
    }
    public void SortOnID() {
        jobs.sort(new SortJobOnID());
    }
    void print_job() {
        System.out.println("id\ttype\tsize\tPins_allocated\tstart_time\tend_time\tExecution_time");
        for (int j = 0; j < this.jobs.size(); j++) {
            System.out.println(jobs.get(j).job_id + "\t" + jobs.get(j).type + "\t" + jobs.get(j).dataset_size + "\t" + jobs.get(j).allocated_pins + "\t" + jobs.get(j).start_time + "\t" + jobs.get(j).end_time + "\t" + jobs.get(j).executionTime + "\t" + jobs.get(j).PM);
        }
    }
    void revers(int pins) {
        ArrayList<Job> newJobs = null;
        if (pins == 0) {
            newJobs = jobs;
        }
        if (pins == 1) {
            newJobs = jobs_1;
        }
        if (pins == 2) {
            newJobs = jobs_2;
        }
        if (pins == 3) {
            newJobs = jobs_3;
        }
        if (pins == 4) {
            newJobs = jobs_4;
        }
        if (pins == -1) {
            newJobs = jobs_p;
        }
        ArrayList<Job> temp = new ArrayList<Job>();
        for (int i = newJobs.size() - 1; i >= 0; i--) {
            temp.add(newJobs.get(i));
        }
        if (pins == 0) {
            jobs = newJobs;
        }
        if (pins == 1) {
            jobs_1 = newJobs;
        }
        if (pins == 2) {
            jobs_2 = newJobs;
        }
        if (pins == 3) {
            jobs_3 = newJobs;
        }
        if (pins == 4) {
            jobs_4 = newJobs;
        }
        if (pins == -1) {
            jobs_p = newJobs;
        }
    }
}
class Cluster {
    public int Cluster_id;
    public int total_execution_time;
    public int total_number_of_jobs;
}
class Cloud {
    ArrayList<PhysicalMachine> cloud_PMs = null;
    ArrayList<Job> current_Jobs = null;
    public int timer = 0;
    public int totalPMs = 0;
    public int totalMigrationsInCloud = 0;
    public int total_waste_cores = 0;
    private int getRunningPMs() {
        int sum = 0;        
        for (int i = 0; i < cloud_PMs.size(); i++) {
            PhysicalMachine pm = cloud_PMs.get(i);
            if (pm.used_capacity > 0) {
                sum++;
            }
        }
        return sum;
    }
    private int get_PM_Index_WRT_Job(Job job) {
        for (int i = 0; i < cloud_PMs.size(); i++) {
            PhysicalMachine pm = cloud_PMs.get(i);
            if (pm.pm_id == job.PM) {
                return i;
            }
        }
        return -1;
    }
    private int get_Waste_Cores(){
        int cores = 0;
        for(int i = 0; i < cloud_PMs.size(); i++)
        {
            if(cloud_PMs.get(i).used_capacity != 0)
                cores += (cloud_PMs.get(i).max_capacity - cloud_PMs.get(i).used_capacity);
        }
        return cores;
    }
    public boolean startCloud(ArrayList<Job> unallocatedJobs) {
        cloud_PMs = new ArrayList<PhysicalMachine>();
        if(ContainerPinsSelection.sort_Jobs_In_Cloud_Startup.equals("execution_time"))
            Collections.sort(unallocatedJobs, new SortJobOnExecctionTime());
        else if(ContainerPinsSelection.sort_Jobs_In_Cloud_Startup.equals("pin"))
            Collections.sort(unallocatedJobs, new SortJobOnPin());
        else if(ContainerPinsSelection.sort_Jobs_In_Cloud_Startup.equals("id"))
            Collections.sort(unallocatedJobs, new SortJobOnID());
        for (int i = 0; i < unallocatedJobs.size(); i++) {
            Job job = unallocatedJobs.get(i);
            for (int x = 0; x < cloud_PMs.size(); x++) {
                PhysicalMachine pm = cloud_PMs.get(x);
                if (job.start_time == -1 && (pm.used_capacity + job.allocated_pins <= pm.max_capacity)) {
                    pm.used_capacity = pm.used_capacity + job.allocated_pins;
                    job.start_time = timer;
                    if (job.allocated_pins == 1) {
                        job.end_time = job.start_time + job.pin1_time - 1;
                        job.executionTime = job.pin1_time;
                    } else if (job.allocated_pins == 2) {
                        job.end_time = job.start_time + job.pin2_time - 1;
                        job.executionTime = job.pin2_time;
                    } else if (job.allocated_pins == 3) {
                        job.end_time = job.start_time + job.pin3_time - 1;
                        job.executionTime = job.pin3_time;
                    } else {
                        job.end_time = job.start_time + job.pin4_time - 1;
                        job.executionTime = job.pin4_time;
                    }
                    job.PM = pm.pm_id;
                    job.is_On_Cloud = true;
                    pm.current_jobs.add(job);
                    cloud_PMs.remove(x);
                    cloud_PMs.add(x, pm);
                    break;
                }
            }
            if (job.start_time == -1) {
                PhysicalMachine pm = new PhysicalMachine(cloud_PMs.size() + 1, ContainerPinsSelection.cores);
                cloud_PMs.add(pm);
                i--;
            }
            else {
                unallocatedJobs.remove(i);
                unallocatedJobs.add(i, job);
            }
        }
        for (int i = 0; i < unallocatedJobs.size(); i++) {
            Job x = unallocatedJobs.get(i);
            if (x.is_On_Cloud != true || x.start_time == -1) {
                System.out.println("Error in cloud setup");
                return false;
            }
        }
        current_Jobs = unallocatedJobs;
        totalPMs += getRunningPMs();
        total_waste_cores += get_Waste_Cores();
        timer++;
        return true;
    }
    public static int find_Job(ArrayList<Job> jobs, Job j) {
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            if (job.job_id == j.job_id) {
                return i;
            }
        }
        return -1;
    }
    public void stopCloud(Result res) {
        res.totalTimeForCloud = timer;
        res.totalPM_On_Cloud = totalPMs/timer;
        res.total_Migrations_In_Cloud = totalMigrationsInCloud;
    }
    public int migration_Full_Pms_In_Cloud(){
        ArrayList<PhysicalMachine> temp_PMs = ContainerPinsSelection.copyPMArrayList(cloud_PMs);
        ArrayList<Job> temp_Jobs = ContainerPinsSelection.copyJobsArrayList(current_Jobs);
        ArrayList<PhysicalMachine> temp_copy_PMs = ContainerPinsSelection.copyPMArrayList(cloud_PMs);
        ArrayList<Job> temp_copy_Jobs = ContainerPinsSelection.copyJobsArrayList(current_Jobs);
        int count = 0;
        int remaining_time = 0;
        int mig = 0;
        ContainerPinsSelection.sort_PMs_UsedCapcity_Ascending_Range(temp_PMs, 0, temp_PMs.size());
        for (int x = 0; x < (temp_PMs.size() - 1); x++) {
            PhysicalMachine x_PM = temp_PMs.get(x);
            int x_PM_jobs = x_PM.current_jobs.size();
            count = 0;            
            for (int j = 0; j < x_PM.current_jobs.size(); j++) {
                Job current_job = x_PM.current_jobs.get(j);
                remaining_time = current_job.end_time - timer;
                if (remaining_time <= ContainerPinsSelection.threshold_Timer_For_Job_Migration_In_Cloud) {
                    break;
                }
                ContainerPinsSelection.sort_PMs_FreeCapcity_Ascending_Range(temp_PMs, x + 1, temp_PMs.size());
                for (int y = x + 1; y < temp_PMs.size(); y++) {
                    PhysicalMachine y_PM = temp_PMs.get(y);
                    if(y_PM.used_capacity == 0)
                    {
                        continue;
                    }
                    if ((y_PM.used_capacity + current_job.allocated_pins) <= y_PM.max_capacity) {
                        y_PM.used_capacity += current_job.allocated_pins;
                        y_PM.current_jobs.add(current_job);
                        current_job.PM = y_PM.pm_id;
                        temp_PMs.remove(y);
                        temp_PMs.add(y, y_PM);
                        int job_index = find_Job(temp_Jobs, current_job);
                        temp_Jobs.remove(job_index);
                        temp_Jobs.add(job_index, current_job);
                        x_PM.used_capacity -= current_job.allocated_pins;
                        x_PM.current_jobs.remove(j);
                        temp_PMs.remove(x);
                        temp_PMs.add(x, x_PM);
                        x_PM = temp_PMs.get(x);
                        count++;
                        j--;
                        break;
                    }
                }
            }
            if(count == x_PM_jobs)
            {
                x_PM.used_capacity = 0;
                x_PM.current_jobs.clear();
                temp_PMs.remove(x);
                temp_PMs.add(x, x_PM);
                temp_copy_PMs = ContainerPinsSelection.copyPMArrayList(temp_PMs);
                temp_copy_Jobs = ContainerPinsSelection.copyJobsArrayList(temp_Jobs);
                mig = mig + count;
                ContainerPinsSelection.sort_PMs_UsedCapcity_Ascending_Range(temp_PMs, x + 1, temp_PMs.size());
            }
            else
            {
                cloud_PMs = temp_copy_PMs;
                current_Jobs = temp_copy_Jobs;
                return mig;
            }
        }
        cloud_PMs = temp_copy_PMs;
        current_Jobs = temp_copy_Jobs;
        return mig;
    }
    public boolean isAnyRemainingJob() {
        int job_index = -1;
        int pm_index = -1;
        for (int j = 0; j < current_Jobs.size(); j++) {
            Job job = current_Jobs.get(j);
            if (job.isComplete == false) {
                if (job.end_time == timer) {
                    job.isComplete = true;
                    pm_index = get_PM_Index_WRT_Job(job);
                    if (pm_index == -1) {
                        System.out.println("Issue in finding job wrt PM");
                    }
                    PhysicalMachine currentPM = cloud_PMs.get(pm_index);
                    currentPM.used_capacity -= job.allocated_pins;
                    job_index = ContainerPinsSelection.getJobIndex_In_PM(currentPM, job);
                    if (job_index == -1) {
                        System.out.println("Job not found in cloud");
                    }
                    currentPM.current_jobs.remove(job_index);
                    cloud_PMs.remove(pm_index);
                    cloud_PMs.add(pm_index, currentPM);
                    current_Jobs.remove(j);
                    current_Jobs.add(j, job);
                }
            }
        }
        if (ContainerPinsSelection.count_Migrations_In_Cloud == true) {
            int mig = migration_Full_Pms_In_Cloud();
            totalMigrationsInCloud += mig;
        }
        totalPMs += getRunningPMs();
        total_waste_cores += get_Waste_Cores();
        timer++;        
        for (int j = 0; j < current_Jobs.size(); j++) {
            if (current_Jobs.get(j).isComplete == false) {
                return true;
            }
        }
        return false;
    }
    public boolean migrateJobsToCloud(ArrayList<Job> unallocatedJobs) {
        boolean flag = false;
        for (int i = 0; i < unallocatedJobs.size(); i++) {
            Job job = unallocatedJobs.get(i);
            for (int x = 0; x < cloud_PMs.size(); x++) {
                PhysicalMachine pm = cloud_PMs.get(x);
                if (pm.used_capacity + job.allocated_pins <= pm.max_capacity) {
                    pm.used_capacity = pm.used_capacity + job.allocated_pins;
                    job.PM = pm.pm_id;
                    job.is_On_Cloud = true;
                    pm.current_jobs.add(job);
                    cloud_PMs.remove(x);
                    cloud_PMs.add(x, pm);
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                PhysicalMachine pm = new PhysicalMachine(cloud_PMs.size() + 1, ContainerPinsSelection.cores);
                cloud_PMs.add(pm);
                i--;
            }
            else
            {
                unallocatedJobs.remove(i);
                unallocatedJobs.add(i, job);
            }
            flag = false;
        }
        for (int i = 0; i < unallocatedJobs.size(); i++) {
            Job x = unallocatedJobs.get(i);
            if (x.is_On_Cloud == false) {
                System.out.println("Error in cloud setup");
                return false;
            }
        }
        for(int i = 0; i < unallocatedJobs.size(); i++)
        {
            current_Jobs.add(unallocatedJobs.get(i));
        }
        return true;
    }
    public ArrayList<Job> migrateJobToLocalEdge(int freeCores) {
        ArrayList<Job> migrate_Job = new ArrayList<Job>();
        ContainerPinsSelection.sort_PMs_UsedCapcity_Ascending_Range(cloud_PMs, 0, cloud_PMs.size());
        for (int i = 0; i < cloud_PMs.size(); i++) {
            PhysicalMachine pm = cloud_PMs.get(i);
            for (int j = 0; j < pm.current_jobs.size(); j++) {
                Job job = pm.current_jobs.get(j);
                if(ContainerPinsSelection.pre_Offloading == true && ContainerPinsSelection.is_Pre_Offloading_Required(job) == true)
                    continue;
                if (job.allocated_pins <= freeCores) {
                    job.PM = -1;
                    job.is_On_Cloud = false;
                    pm.used_capacity -= job.allocated_pins;
                    pm.current_jobs.remove(j);
                    for (int x = 0; x < current_Jobs.size(); x++) {
                        if (current_Jobs.get(x).job_id == job.job_id) {
                            current_Jobs.remove(x);
                            break;
                        }
                    }
                    migrate_Job.add(job);
                    freeCores -= job.allocated_pins;
                    cloud_PMs.remove(i);
                    cloud_PMs.add(i, pm);
                }
            }
        }
        return migrate_Job;
    }
}
class ContainerPinsSelection {
    public static boolean count_Free_PMs_Time = true;
    public static boolean count_Every_Free_PMs_Time = true;
    public static String count_Free_PMs_Time_WRT = "everyTime";
    public static boolean count_Every_Free_Cores_Time = true;
    public static String count_Free_Cores_Time_WRT = "everyTime";
    public static boolean count_Migrations = true;
    public static boolean count_Migrations_In_Cloud = true;
    public static boolean mig_using_usedCapacity = false;
    public static boolean mig_using_usedCapacity_new = true;
    public static int timer_Count_Migrations = 1;
    public static int threshold_Timer_For_Job_Migration = -1;
    public static int threshold_Timer_For_Job_Migration_In_Cloud = -1;    
    public static String migration_WRT = "heuristic_Free_PM";
    public static String cluster_file_name = "tuple_cluster_";
    public static String jobs_file_name = "tuple";
    public static String PMs_cores_file_name = "tuple_PMs_cores_1000";
    public static int number_Of_PMs = 1000;
    public static int cores = 8;
    public static int[] numberOfJobsArray = {1, 2};
    public static boolean enable_mig_cloud_to_local_FCFS = true;
    public static String sort_Jobs_In_Cloud_Startup = "execution_time"; 
    public static boolean shuffle_PMs_FCFS = false;
    public static boolean free_Cores_Seperate_Files = false;
    public static boolean clustering = true;
    public static boolean is_New_Cluster_For_Proposed = false;
    public static String sort_Clusters_According_To = "execution_time";
    public static boolean isOffloading = true;
    public static boolean pre_Offloading = true;
    public static boolean PMs_Hetro = true;
    public static boolean FCFS = false;
    public static boolean SJF = true;
    public static boolean LJF = false;
    public static String display_Form = "vertical";
    public static double threshold_For_Proposed_FCFS = 1.25;
    public static double threshold_For_Proposed_SJF = 1.25;
    public static double threshold_For_Proposed_LJF = 1.25;    
    public static String [] tuple_types = {"Adrupt", "Medical", "Large",
        "Multimedia", "Bulk", "SmallTextual", "LocationBased"}; 
    public static double Min(double d1, double d2, double d3) {
        if (d1 <= d2 && d1 <= d3) {
            return d1;
        } else if (d2 <= d1 && d2 <= d3) {
            return d2;
        } else {
            return d3;
        }
    }
    public static ArrayList<Job> copyJobsArrayList(ArrayList<Job> sourceList) {
        ArrayList<Job> new_jobs = new ArrayList<Job>();
        for (int i = 0; i < sourceList.size(); i++) {
            Job job = new Job(sourceList.get(i));
            new_jobs.add(job);
        }
        return new_jobs;
    }
    public static ArrayList<PhysicalMachine> copyPMArrayList(ArrayList<PhysicalMachine> sourceList) {
        ArrayList<PhysicalMachine> new_pms = new ArrayList<PhysicalMachine>();
        for (int i = 0; i < sourceList.size(); i++) {
            PhysicalMachine pm = new PhysicalMachine(sourceList.get(i));
            new_pms.add(pm);
        }
        return new_pms;
    }
    public static PhysicalMachine[] copyPMSimpleArray(PhysicalMachine[] sourceList) {
        PhysicalMachine [] new_PMs = new PhysicalMachine[sourceList.length];        
        for (int i = 0; i < sourceList.length; i++) {
            PhysicalMachine pm = new PhysicalMachine(sourceList[i]);
            new_PMs[i] = pm;
        }
        return new_PMs;
    }
    public static void copyPMSimpleArray_same_ref(PhysicalMachine[] dest_pm, PhysicalMachine[] sourceList) {
        for (int i = 0; i < sourceList.length; i++) {
            dest_pm[i] = sourceList[i];
        }
    }
    public static double average_percentage_Used_Capacity_Per_Time_Interval(PhysicalMachine [] pm){
        double res = 0;        
        for(int i = 0; i < pm.length; i++)
        {
            res = res + ((pm[i].used_capacity*100)/pm[i].max_capacity);
        }
        res = res/pm.length;
        return res;
    }
    public static void shuffle_PMs(PhysicalMachine [] pms){
        int range = pms.length;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < range; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for(int i = 0; i < range; i++)
        {
            int j = list.get(i);
            PhysicalMachine temp_pm = pms[i];
            pms[i] = pms[j];
            pms[j] = temp_pm;
        }
    }
    public static int tuple_Id(String tuple_type){
        for(int i = 0; i < tuple_types.length; i++)
        {
            if(tuple_types[i].equals(tuple_type))
            {
                return i;
            }
        }
        return -1;
    }
    public static boolean is_Pre_Offloading_Required(Job job){
        if(job.type == tuple_Id("Bulk"))
        {
            return true;
        }
        if(job.type == tuple_Id("Large"))
        {
            return true;
        }
        return false;
    }
    public static Result execute_jobs_proposed_FCFS_MultiPMs(Job_Generator job_gen, int numberOfJobs, PhysicalMachine[] pm) throws FileNotFoundException, IOException {
        if (isOffloading == true) {
            return ContainerPinsSelection.execute_jobs_proposed_FCFS_MultiPMs_Offloading(job_gen, numberOfJobs, pm);
        }
        return null;
    }
    public static Result execute_jobs_proposed_FCFS_MultiPMs_Offloading(Job_Generator job_gen, int numberOfJobs, PhysicalMachine[] pm) {
        Result R = new Result();
        int migrations_to_local = 0;
        double threshold = ContainerPinsSelection.threshold_For_Proposed_FCFS;
        int static_pins = 1;
        int i = 1;
        boolean flag = true;
        int TotalWaitTimeSum = 0;
        int[] TotalWasteCapacity = new int[pm.length];
        int freePMs = 0;
        int totalMigrations = 0;
        int timeIntervalOfFreePMs = 0;
        ArrayList<Job> unallocated_Jobs = new ArrayList<Job>();
        Cloud cloud = null;
        boolean flag_ForCloud_Setup = false;
        boolean flag_stop_cloud = false;
        ArrayList<Integer> free_PM_count = new ArrayList<Integer>();
        for (int x = 0; x < number_Of_PMs; x++) {
            free_PM_count.add(0);
        }
        ArrayList<Integer> free_Cores_count = new ArrayList<Integer>();
        for (int x = 0; x < number_Of_PMs; x++) {
            free_Cores_count.add(0);
        }
        ArrayList<Job> current_job;
        ArrayList<Job> original_jobs;
        if (clustering == true) {
            current_job = job_gen.jobs_p;
            original_jobs = copyJobsArrayList(job_gen.jobs_p);
        } else {
            current_job = job_gen.jobs;
            original_jobs = copyJobsArrayList(job_gen.jobs);
        }
        int size = current_job.size();
        for (int j = 0; j < size; j++) {
            Job job = current_job.get(j);
            if (job.isComplete == false) {
                static_pins = job.allocated_pins;
                sort_PMs_FreeCapcity_Ascending(pm);
                for (int x = 0; x < pm.length; x++) {
                    if (job.start_time == -1 && (pm[x].used_capacity + static_pins <= pm[x].max_capacity)) {
                        pm[x].used_capacity = pm[x].used_capacity + static_pins;
                        job.start_time = i;
                        job.PM = pm[x].pm_id;
                        pm[x].current_jobs.add(job);
                        if (static_pins == 1) {
                            job.end_time = job.start_time + job.pin1_time - 1;
                            job.executionTime = job.pin1_time;
                        } else if (static_pins == 2) {
                            job.end_time = job.start_time + job.pin2_time - 1;
                            job.executionTime = job.pin2_time;
                        } else if (static_pins == 3) {
                            job.end_time = job.start_time + job.pin3_time - 1;
                            job.executionTime = job.pin3_time;
                        } else {
                            job.end_time = job.start_time + job.pin4_time - 1;
                            job.executionTime = job.pin4_time;
                        }
                        TotalWaitTimeSum += job.start_time - 1;
                        current_job.remove(j);
                        current_job.add(j, job);
                    }
                }
            }
            if (isOffloading == true && job.start_time == -1) {
                unallocated_Jobs.add(job);
                current_job.remove(j);
                j--;
            }
            size = current_job.size();
        }
        if (isOffloading == true && i == 1 && unallocated_Jobs.size() > 0) {
            cloud = new Cloud();
            if (cloud.startCloud(unallocated_Jobs) == true) {
                System.out.println("Cloud setup is started");
                flag_ForCloud_Setup = true;
            } else {
                flag_ForCloud_Setup = false;
            }
        }
        while (flag == true || (flag_stop_cloud == false && flag_ForCloud_Setup == true)) {
            if (isOffloading == true && i != 1 && flag_ForCloud_Setup == true) {
                if (flag_stop_cloud == false) {
                    if (cloud.isAnyRemainingJob() == false) {
                        System.out.println("Cloud setup is started for remaining");
                        cloud.stopCloud(R);
                        flag_stop_cloud = true;
                    }
                }
            }
            int job_index = -1;
            int pm_index = -1;
            for (int j = 0; j < size; j++) {
                Job job = current_job.get(j);
                if (current_job.get(j).isComplete == false) {
                    if (job.end_time == i) {
                        job.isComplete = true;
                        pm_index = getPM_Index(pm, job.PM);
                        pm[pm_index].used_capacity = pm[pm_index].used_capacity - job.allocated_pins;
                        job_index = getJobIndex_In_PM(pm[pm_index], job);
                        if (job_index == -1) {
                            System.out.println("Job not found 2");
                        }
                        pm[pm_index].current_jobs.remove(job_index);
                        current_job.remove(j);
                        current_job.add(j, job);
                        if (isOffloading == true && flag_ForCloud_Setup == true && enable_mig_cloud_to_local_FCFS == true) {
                            ArrayList<Job> migrated_jobs = cloud.migrateJobToLocalEdge(pm[pm_index].max_capacity - pm[pm_index].used_capacity);
                            migrations_to_local += migrated_jobs.size();
                            for (int x = 0; x < migrated_jobs.size(); x++) {
                                Job m_job = migrated_jobs.get(x);
                                m_job.is_On_Cloud = false;
                                m_job.PM = pm[pm_index].pm_id;
                                pm[pm_index].used_capacity += m_job.allocated_pins;
                                pm[pm_index].current_jobs.add(m_job);
                                current_job.add(m_job);
                            }
                            size = current_job.size();
                        }
                    }
                }
            }
            if (flag == true && count_Migrations == true && (i % timer_Count_Migrations == 0)) {
                int mig;
                if (clustering == true) {
                    job_gen.jobs_p = current_job;
                    mig = simple_migration(job_gen, -1, numberOfJobs, pm, i);
                } else {
                    job_gen.jobs = current_job;
                    mig = simple_migration(job_gen, static_pins, numberOfJobs, pm, i);
                }
                totalMigrations += mig;
                if (mig != 0) {
                    if (count_Free_PMs_Time == true && count_Free_PMs_Time_WRT.equals("everyMigration")) {
                        int temp = getFreePMs(pm);
                        freePMs += temp;
                        if (temp != 0) {
                            timeIntervalOfFreePMs++;
                        }
                    }
                }
                if (clustering == true) {
                    current_job = job_gen.jobs_p;
                } else {
                    current_job = job_gen.jobs;
                }
            }
            if(flag == true)
            {
                for (int x = 0; x < pm.length; x++) {
                    if(pm[x].used_capacity != 0)
                        TotalWasteCapacity[x] += pm[x].max_capacity - pm[x].used_capacity;
                }
            }
            if (flag == true && count_Free_PMs_Time == true && count_Free_PMs_Time_WRT.equals("everyTime")) {
                int temp = getFreePMs(pm);

                freePMs += temp;
                if (temp != 0) {
                    timeIntervalOfFreePMs++;
                }
            }
            if (flag == true && count_Every_Free_PMs_Time == true && count_Free_PMs_Time_WRT.equals("everyTime")) {
                updateFreePMsData(pm, free_PM_count);
            }
            if (flag == true && count_Every_Free_Cores_Time == true && count_Free_Cores_Time_WRT.equals("everyTime")) {
                updateFreeCoresData(pm, free_Cores_count);
            }
            flag = false;
            for (int j = 0; j < size; j++) {
                if (current_job.get(j).isComplete == false) {
                    flag = true;
                    break;
                }
            }
            if (flag == true) {
                i++;
            }
        }
        R.totalTime = i;
        R.local_migrations_from_cloud_to_local = migrations_to_local;
        R.averageWaitTime = (double) TotalWaitTimeSum / (double) numberOfJobs;
        int waste = 0;
        for (int x = 0; x < pm.length; x++) {
            waste += TotalWasteCapacity[x];
        }
        R.averageWasteCapacity = (double) waste / (double) R.totalTime;
        if (count_Migrations == true) {
            R.avergaeNumberOfMigrations = (double) totalMigrations;
        } else {
            R.avergaeNumberOfMigrations = -1;
        }
        System.out.println("Total_Time_Taken\t" + R.totalTime);
        if (clustering == true) {
            job_gen.jobs_p = original_jobs;
        } else {
            job_gen.jobs = original_jobs;
        }
        return R;
    }    
    public static Result execute_jobs_proposed_SJF_MultiPMs(Job_Generator job_gen, int numberOfJobs, PhysicalMachine[] pm) throws FileNotFoundException, IOException {
        if (isOffloading == true) {
            return ContainerPinsSelection.execute_jobs_proposed_SJF_MultiPMs_Offloading(job_gen, numberOfJobs, pm);
        }
        return null;
    }
    public static Result execute_jobs_proposed_SJF_MultiPMs_Offloading(Job_Generator job_gen, int numberOfJobs, PhysicalMachine[] pm) {
        Result R = new Result();
        int migrations_to_local = 0;
        double threshold = ContainerPinsSelection.threshold_For_Proposed_SJF;
        int static_pins = 1;
        int i = 1;
        boolean flag = true;
        int TotalWaitTimeSum = 0;
        int[] TotalWasteCapacity = new int[pm.length];
        int freePMs = 0;
        int totalMigrations = 0;
        int timeIntervalOfFreePMs = 0;
        ArrayList<Job> unallocated_Jobs = new ArrayList<Job>();
        Cloud cloud = null;
        boolean flag_ForCloud_Setup = false;
        boolean flag_stop_cloud = false;
        ArrayList<Integer> free_PM_count = new ArrayList<Integer>();
        for (int x = 0; x < number_Of_PMs; x++) {
            free_PM_count.add(0);
        }
        ArrayList<Integer> free_Cores_count = new ArrayList<Integer>();
        for (int x = 0; x < number_Of_PMs; x++) {
            free_Cores_count.add(0);
        }
        ArrayList<Job> current_job;
        ArrayList<Job> original_jobs;
        if (clustering == true) {
            job_gen.SortOnExecutionTime(-1);
            current_job = job_gen.jobs_p;
            original_jobs = copyJobsArrayList(job_gen.jobs_p);
        } else {
            job_gen.SortOnExecutionTime(0);
            current_job = job_gen.jobs;
            original_jobs = copyJobsArrayList(job_gen.jobs);
        }
        int size = current_job.size();
        for (int j = 0; j < size; j++) {
            Job job = current_job.get(j);
            if (job.isComplete == false) {
                static_pins = job.allocated_pins;
                sort_PMs_FreeCapcity_Ascending(pm);
                for (int x = 0; x < pm.length; x++) {
                    if (job.start_time == -1 && (pm[x].used_capacity + static_pins <= pm[x].max_capacity)) {
                        pm[x].used_capacity = pm[x].used_capacity + static_pins;
                        job.start_time = i;
                        job.PM = pm[x].pm_id;
                        pm[x].current_jobs.add(job);
                        if (static_pins == 1) {
                            job.end_time = job.start_time + job.pin1_time - 1;
                            job.executionTime = job.pin1_time;
                        } else if (static_pins == 2) {
                            job.end_time = job.start_time + job.pin2_time - 1;
                            job.executionTime = job.pin2_time;
                        } else if (static_pins == 3) {
                            job.end_time = job.start_time + job.pin3_time - 1;
                            job.executionTime = job.pin3_time;
                        } else {
                            job.end_time = job.start_time + job.pin4_time - 1;
                            job.executionTime = job.pin4_time;
                        }
                        TotalWaitTimeSum += job.start_time - 1;
                        current_job.remove(j);
                        current_job.add(j, job);
                    }
                }
            }
            if (isOffloading == true && job.start_time == -1) {
                unallocated_Jobs.add(job);
                current_job.remove(j);
                j--;
            }
            size = current_job.size();
        }
        if (isOffloading == true && i == 1 && unallocated_Jobs.size() > 0) {
            cloud = new Cloud();
            if (cloud.startCloud(unallocated_Jobs) == true) {
                System.out.println("Cloud setup is started");
                flag_ForCloud_Setup = true;
            } else {
                flag_ForCloud_Setup = false;
            }
        }
        while (flag == true || (flag_stop_cloud == false && flag_ForCloud_Setup == true)) {
            if (isOffloading == true && i != 1 && flag_ForCloud_Setup == true) {
                if (flag_stop_cloud == false) {
                    if (cloud.isAnyRemainingJob() == false) {
                        System.out.println("Cloud setup is started for remaining");
                        cloud.stopCloud(R);
                        flag_stop_cloud = true;
                    }
                }
            }
            int job_index = -1;
            int pm_index = -1;
            for (int j = 0; j < size; j++) {
                Job job = current_job.get(j);
                if (current_job.get(j).isComplete == false) {
                    if (job.end_time == i) {
                        job.isComplete = true;
                        pm_index = getPM_Index(pm, job.PM);
                        pm[pm_index].used_capacity = pm[pm_index].used_capacity - job.allocated_pins;
                        job_index = getJobIndex_In_PM(pm[pm_index], job);
                        if (job_index == -1) {
                            System.out.println("Job not found 2");
                        }
                        pm[pm_index].current_jobs.remove(job_index);
                        current_job.remove(j);
                        current_job.add(j, job);
                        if (isOffloading == true && flag_ForCloud_Setup == true && enable_mig_cloud_to_local_FCFS == true) {
                            ArrayList<Job> migrated_jobs = cloud.migrateJobToLocalEdge(pm[pm_index].max_capacity - pm[pm_index].used_capacity);
                            migrations_to_local += migrated_jobs.size();
                            for (int x = 0; x < migrated_jobs.size(); x++) {
                                Job m_job = migrated_jobs.get(x);
                                m_job.is_On_Cloud = false;
                                m_job.PM = pm[pm_index].pm_id;
                                pm[pm_index].used_capacity += m_job.allocated_pins;
                                pm[pm_index].current_jobs.add(m_job);
                                current_job.add(m_job);
                            }
                            size = current_job.size();
                        }
                    }
                }
            }
            if (flag == true && count_Migrations == true && (i % timer_Count_Migrations == 0)) {
                int mig;
                if (clustering == true) {
                    job_gen.jobs_p = current_job;
                    mig = simple_migration(job_gen, -1, numberOfJobs, pm, i);
                } else {
                    job_gen.jobs = current_job;
                    mig = simple_migration(job_gen, static_pins, numberOfJobs, pm, i);
                }
                totalMigrations += mig;
                if (mig != 0) {
                    if (count_Free_PMs_Time == true && count_Free_PMs_Time_WRT.equals("everyMigration")) {
                        int temp = getFreePMs(pm);
                        freePMs += temp;
                        if (temp != 0) {
                            timeIntervalOfFreePMs++;
                        }
                    }
                }
                if (clustering == true) {
                    current_job = job_gen.jobs_p;
                } else {
                    current_job = job_gen.jobs;
                }
            }
            if(flag == true)
            {
                for (int x = 0; x < pm.length; x++) {
                    if(pm[x].used_capacity != 0)
                        TotalWasteCapacity[x] += pm[x].max_capacity - pm[x].used_capacity;
                }
            }

            if (flag == true && count_Free_PMs_Time == true && count_Free_PMs_Time_WRT.equals("everyTime")) {
                int temp = getFreePMs(pm);
                freePMs += temp;
                if (temp != 0) {
                    timeIntervalOfFreePMs++;
                }
            }
            if (flag == true && count_Every_Free_PMs_Time == true && count_Free_PMs_Time_WRT.equals("everyTime")) {
                updateFreePMsData(pm, free_PM_count);
            }
            if (flag == true && count_Every_Free_Cores_Time == true && count_Free_Cores_Time_WRT.equals("everyTime")) {
                updateFreeCoresData(pm, free_Cores_count);
            }
            flag = false;
            for (int j = 0; j < size; j++) {
                if (current_job.get(j).isComplete == false) {
                    flag = true;
                    break;
                }
            }
            if (flag == true) {
                i++;
            }
        }
        R.totalTime = i;
        R.local_migrations_from_cloud_to_local = migrations_to_local;
        R.averageWaitTime = (double) TotalWaitTimeSum / (double) numberOfJobs;
        int waste = 0;
        for (int x = 0; x < pm.length; x++) {
            waste += TotalWasteCapacity[x];
        }
        R.averageWasteCapacity = (double) waste / (double) R.totalTime;
        if (count_Migrations == true) {
            R.avergaeNumberOfMigrations = (double) totalMigrations;
        } else {
            R.avergaeNumberOfMigrations = -1;
        }
        System.out.println("Total_Time_Taken\t" + R.totalTime);
        if (clustering == true) {
            job_gen.jobs_p = original_jobs;
        } else {
            job_gen.jobs = original_jobs;
        }
        return R;
    }
    public static Result execute_jobs_proposed_LJF_MultiPMs(Job_Generator job_gen, int numberOfJobs, PhysicalMachine[] pm) throws FileNotFoundException, IOException {
        if (isOffloading == true) {
            return ContainerPinsSelection.execute_jobs_proposed_LJF_MultiPMs_Offloading(job_gen, numberOfJobs, pm);
        }
        return null;
    }
    public static Result execute_jobs_proposed_LJF_MultiPMs_Offloading(Job_Generator job_gen, int numberOfJobs, PhysicalMachine[] pm) {
        Result R = new Result();
        int migrations_to_local = 0;
        double threshold = ContainerPinsSelection.threshold_For_Proposed_LJF;
        int static_pins = 1;
        int i = 1;
        boolean flag = true;
        int TotalWaitTimeSum = 0;
        int[] TotalWasteCapacity = new int[pm.length];
        int freePMs = 0;
        int totalMigrations = 0;
        int timeIntervalOfFreePMs = 0;
        ArrayList<Job> unallocated_Jobs = new ArrayList<Job>();
        Cloud cloud = null;
        boolean flag_ForCloud_Setup = false;
        boolean flag_stop_cloud = false;
        ArrayList<Integer> free_PM_count = new ArrayList<Integer>();
        for (int x = 0; x < number_Of_PMs; x++) {
            free_PM_count.add(0);
        }
        ArrayList<Integer> free_Cores_count = new ArrayList<Integer>();
        for (int x = 0; x < number_Of_PMs; x++) {
            free_Cores_count.add(0);
        }
        ArrayList<Job> current_job;
        ArrayList<Job> original_jobs;
        if (clustering == true) {
            job_gen.SortOnExecutionTime(-1);
            job_gen.revers(-1);
            current_job = job_gen.jobs_p;
            original_jobs = copyJobsArrayList(job_gen.jobs_p);
        } else {
            job_gen.SortOnExecutionTime(0);
            job_gen.revers(0);
            current_job = job_gen.jobs;
            original_jobs = copyJobsArrayList(job_gen.jobs);
        }
        int size = current_job.size();
        for (int j = 0; j < size; j++) {
            Job job = current_job.get(j);
            if (job.isComplete == false) {
                static_pins = job.allocated_pins;
                sort_PMs_FreeCapcity_Ascending(pm);
                for (int x = 0; x < pm.length; x++) {
                    if (job.start_time == -1 && (pm[x].used_capacity + static_pins <= pm[x].max_capacity)) {
                        pm[x].used_capacity = pm[x].used_capacity + static_pins;
                        job.start_time = i;
                        job.PM = pm[x].pm_id;
                        pm[x].current_jobs.add(job);
                        if (static_pins == 1) {
                            job.end_time = job.start_time + job.pin1_time - 1;
                            job.executionTime = job.pin1_time;
                        } else if (static_pins == 2) {
                            job.end_time = job.start_time + job.pin2_time - 1;
                            job.executionTime = job.pin2_time;
                        } else if (static_pins == 3) {
                            job.end_time = job.start_time + job.pin3_time - 1;
                            job.executionTime = job.pin3_time;
                        } else {
                            job.end_time = job.start_time + job.pin4_time - 1;
                            job.executionTime = job.pin4_time;
                        }
                        TotalWaitTimeSum += job.start_time - 1;
                        current_job.remove(j);
                        current_job.add(j, job);
                    }
                }
            }
            if (isOffloading == true && job.start_time == -1) {
                unallocated_Jobs.add(job);
                current_job.remove(j);
                j--;
            }
            size = current_job.size();
        }
        if (isOffloading == true && i == 1 && unallocated_Jobs.size() > 0) {
            cloud = new Cloud();
            if (cloud.startCloud(unallocated_Jobs) == true) {
                System.out.println("Cloud setup is started");
                flag_ForCloud_Setup = true;
            } else {
                flag_ForCloud_Setup = false;
            }
        }
        while (flag == true || (flag_stop_cloud == false && flag_ForCloud_Setup == true)) {
            if (isOffloading == true && i != 1 && flag_ForCloud_Setup == true) {
                if (flag_stop_cloud == false) {
                    if (cloud.isAnyRemainingJob() == false) {
                        System.out.println("Cloud setup is started for remaining");
                        cloud.stopCloud(R);
                        flag_stop_cloud = true;
                    }
                }
            }
            int job_index = -1;
            int pm_index = -1;
            for (int j = 0; j < size; j++) {
                Job job = current_job.get(j);
                if (current_job.get(j).isComplete == false) {
                    if (job.end_time == i) {
                        job.isComplete = true;
                        pm_index = getPM_Index(pm, job.PM);
                        pm[pm_index].used_capacity = pm[pm_index].used_capacity - job.allocated_pins;
                        job_index = getJobIndex_In_PM(pm[pm_index], job);
                        if (job_index == -1) {
                            System.out.println("Job not found 2");
                        }
                        pm[pm_index].current_jobs.remove(job_index);
                        current_job.remove(j);
                        current_job.add(j, job);
                        if (isOffloading == true && flag_ForCloud_Setup == true && enable_mig_cloud_to_local_FCFS == true) {
                            ArrayList<Job> migrated_jobs = cloud.migrateJobToLocalEdge(pm[pm_index].max_capacity - pm[pm_index].used_capacity);
                            migrations_to_local += migrated_jobs.size();
                            for (int x = 0; x < migrated_jobs.size(); x++) {
                                Job m_job = migrated_jobs.get(x);
                                m_job.is_On_Cloud = false;
                                m_job.PM = pm[pm_index].pm_id;
                                pm[pm_index].used_capacity += m_job.allocated_pins;
                                pm[pm_index].current_jobs.add(m_job);
                                current_job.add(m_job);
                            }
                            size = current_job.size();
                        }
                    }
                }
            }
            if (flag == true && count_Migrations == true && (i % timer_Count_Migrations == 0)) {
                int mig;
                if (clustering == true) {
                    job_gen.jobs_p = current_job;
                    mig = simple_migration(job_gen, -1, numberOfJobs, pm, i);
                } else {
                    job_gen.jobs = current_job;
                    mig = simple_migration(job_gen, static_pins, numberOfJobs, pm, i);
                }
                totalMigrations += mig;
                if (mig != 0) {
                    if (count_Free_PMs_Time == true && count_Free_PMs_Time_WRT.equals("everyMigration")) {
                        int temp = getFreePMs(pm);
                        freePMs += temp;
                        if (temp != 0) {
                            timeIntervalOfFreePMs++;
                        }
                    }
                }
                if (clustering == true) {
                    current_job = job_gen.jobs_p;
                } else {
                    current_job = job_gen.jobs;
                }
            }
            if(flag == true)
            {
                for (int x = 0; x < pm.length; x++) {
                    if(pm[x].used_capacity != 0)
                        TotalWasteCapacity[x] += pm[x].max_capacity - pm[x].used_capacity;
                }
            }
            if (flag == true && count_Free_PMs_Time == true && count_Free_PMs_Time_WRT.equals("everyTime")) {
                int temp = getFreePMs(pm);

                freePMs += temp;
                if (temp != 0) {
                    timeIntervalOfFreePMs++;
                }
            }
            if (flag == true && count_Every_Free_PMs_Time == true && count_Free_PMs_Time_WRT.equals("everyTime")) {
                updateFreePMsData(pm, free_PM_count);
            }
            if (flag == true && count_Every_Free_Cores_Time == true && count_Free_Cores_Time_WRT.equals("everyTime")) {
                updateFreeCoresData(pm, free_Cores_count);
            }
            flag = false;
            for (int j = 0; j < size; j++) {
                if (current_job.get(j).isComplete == false) {
                    flag = true;
                    break;
                }
            }
            if (flag == true) {
                i++;
            }
        }
        R.totalTime = i;
        R.local_migrations_from_cloud_to_local = migrations_to_local;
        R.averageWaitTime = (double) TotalWaitTimeSum / (double) numberOfJobs;
        int waste = 0;
        for (int x = 0; x < pm.length; x++) {
            waste += TotalWasteCapacity[x];
        }
        R.averageWasteCapacity = (double) waste / (double) R.totalTime;
        if (count_Migrations == true) {
            R.avergaeNumberOfMigrations = (double) totalMigrations;
        } else {
            R.avergaeNumberOfMigrations = -1;
        }
        System.out.println("Total_Time_Taken\t" + R.totalTime);
        if (clustering == true) {
            job_gen.jobs_p = original_jobs;
        } else {
            job_gen.jobs = original_jobs;
        }
        return R;
    }
    public static void sort_PMs_UsedCapcity_Descending_Range(PhysicalMachine[] pm, int start, int end) {
        int free1, free2, index;
        PhysicalMachine temp_PM;
        for (int i = start; i < (end - 1); i++) {
            free1 = pm[i].used_capacity;
            index = i;
            for (int j = (i + 1); j < end; j++) {
                free2 = pm[i].used_capacity;
                if (free1 < free2) {
                    index = j;
                }
            }
            temp_PM = pm[index];
            pm[index] = pm[i];
            pm[i] = temp_PM;
        }
    }
    public static void sort_PMs_UsedCapcity_Descending_Range(ArrayList<PhysicalMachine> pm, int start, int end) {
        int free1, free2, index;
        PhysicalMachine temp_PM;
        for (int i = start; i < (end - 1); i++) {
            free1 = pm.get(i).used_capacity;            
            index = i;
            for (int j = (i + 1); j < end; j++) {
                free2 = pm.get(j).used_capacity;
                if (free1 < free2) {
                    index = j;
                }
            }
            temp_PM = pm.get(index);
            pm.remove(index);
            pm.add(index, pm.get(i));
            pm.remove(i);
            pm.add(i, temp_PM);
        }
    }
    public static void sort_PMs_UsedCapcity_Descending(PhysicalMachine[] pm) {
        sort_PMs_UsedCapcity_Descending_Range(pm, 0, pm.length);
    }
    public static void sort_PMs_UsedCapcity_Ascending_Range(PhysicalMachine[] pm, int start, int end) {
        int free1, free2, index;
        PhysicalMachine temp_PM;
        for (int i = start; i < (end - 1); i++) {
            free1 = pm[i].used_capacity;
            index = i;
            for (int j = (i + 1); j < end; j++) {
                free2 = pm[j].used_capacity;
                if (free1 > free2) {
                    index = j;
                }
            }
            temp_PM = pm[index];
            pm[index] = pm[i];
            pm[i] = temp_PM;
        }
    }
    public static void sort_PMs_UsedCapcity_Ascending_Range(ArrayList<PhysicalMachine> pm, int start, int end) {
        int free1, free2, index;
        PhysicalMachine temp_PM;
        for (int i = start; i < (end - 1); i++) {
            free1 = pm.get(i).used_capacity;
            index = i;
            for (int j = (i + 1); j < end; j++) {
                free2 = pm.get(j).used_capacity;
                if (free1 > free2) {
                    index = j;
                }
            }
            temp_PM = pm.get(index);
            pm.remove(index);
            pm.add(index, pm.get(i));
            pm.remove(pm.get(i));
            pm.add(i, temp_PM);
        }
    }
    public static void sort_PMs_UsedCapcity_Ascending(PhysicalMachine[] pm) {
        sort_PMs_UsedCapcity_Ascending_Range(pm, 0, pm.length);
    } 
    public static void sort_PMs_FreeCapcity_Descending_Range(PhysicalMachine[] pm, int start, int end) {
        int free1, free2, index;
        PhysicalMachine temp_PM;
        for (int i = start; i < (end - 1); i++) {
            free1 = (pm[i].max_capacity - pm[i].used_capacity)/pm[i].max_capacity;
            index = i;
            for (int j = (i + 1); j < end; j++) {
                free2 = (pm[j].max_capacity - pm[j].used_capacity)/pm[j].max_capacity;
                if (free1 < free2) {
                    index = j;
                }
            }
            temp_PM = pm[index];
            pm[index] = pm[i];
            pm[i] = temp_PM;
        }
    }
    public static void sort_PMs_FreeCapcity_Descending_Range(ArrayList<PhysicalMachine> pm, int start, int end) {
        int free1, free2, index;
        PhysicalMachine temp_PM;
        for (int i = start; i < (end - 1); i++) {
            free1 = (pm.get(i).max_capacity - pm.get(i).used_capacity)/pm.get(i).max_capacity;
            index = i;
            for (int j = (i + 1); j < end; j++) {
                free2 = (pm.get(j).max_capacity - pm.get(j).used_capacity)/pm.get(j).max_capacity;
                if (free1 < free2) {
                    index = j;
                }
            }
            temp_PM = pm.get(index);
            pm.remove(index);
            pm.add(index, pm.get(i));
            pm.remove(i);
            pm.add(i, temp_PM);
        }
    }
    public static void sort_PMs_FreeCapcity_Descending(PhysicalMachine[] pm) {
        sort_PMs_FreeCapcity_Descending_Range(pm, 0, pm.length);
    }
    public static void sort_PMs_FreeCapcity_Ascending_Range(PhysicalMachine[] pm, int start, int end) {
        int free1, free2, index;
        PhysicalMachine temp_PM;
        for (int i = start; i < (end - 1); i++) {
            free1 = (pm[i].max_capacity - pm[i].used_capacity)/pm[i].max_capacity;
            index = i;
            for (int j = (i + 1); j < end; j++) {
                free2 = (pm[j].max_capacity - pm[j].used_capacity)/pm[j].max_capacity;
                if (free1 > free2) {
                    index = j;
                }
            }
            temp_PM = pm[index];
            pm[index] = pm[i];
            pm[i] = temp_PM;
        }
    }
    public static void sort_PMs_FreeCapcity_Ascending_Range(ArrayList<PhysicalMachine> pm, int start, int end) {
        int free1, free2, index;
        PhysicalMachine temp_PM;
        for (int i = start; i < (end - 1); i++) {
            free1 = (pm.get(i).max_capacity - pm.get(i).used_capacity)/pm.get(i).max_capacity;
            index = i;
            for (int j = (i + 1); j < end; j++) {
                free2 = (pm.get(j).max_capacity - pm.get(j).used_capacity)/pm.get(j).max_capacity;
                if (free1 > free2) {
                    index = j;
                }
            }
            temp_PM = pm.get(index);
            pm.remove(index);
            pm.add(index, pm.get(i));
            pm.remove(pm.get(i));
            pm.add(i, temp_PM);
        }
    }
    public static void sort_PMs_FreeCapcity_Ascending(PhysicalMachine[] pm) {
        sort_PMs_FreeCapcity_Ascending_Range(pm, 0, pm.length);
    }
    public static ArrayList<Cluster> getUniqueClusters(ArrayList<Job> j) {
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();
        boolean flag = false;
        for (int a = 0; a < j.size(); a++) {
            flag = false;
            Job x = j.get(a);
            for (int b = 0; b < clusters.size(); b++) {
                if (x.cluster_id == clusters.get(b).Cluster_id) {
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                Cluster c = new Cluster();
                c.Cluster_id = x.cluster_id;
                clusters.add(c);
            }
        }
        return clusters;
    }
    public static void initialize_execution_time_total_jobs_of_clusters(ArrayList<Cluster> clusters, ArrayList<Job> j) {
        int total_execution_time = 0;
        int total_jobs = 0;
        for (int a = 0; a < clusters.size(); a++) {
            total_execution_time = 0;
            total_jobs = 0;
            Cluster c = clusters.get(a);
            for (int b = 0; b < j.size(); b++) {
                Job x = j.get(b);
                if (c.Cluster_id == x.cluster_id) {
                    total_jobs++;
                    total_execution_time += x.executionTime;
                }
            }
            c.total_execution_time = total_execution_time;
            c.total_number_of_jobs = total_jobs;
            clusters.remove(a);
            clusters.add(a, c);
        }
    }
    public static void sort_Clusters_Job_Count(ArrayList<Cluster> cluster) {
        Collections.sort(cluster, new SortByClusterJobCount());
    }
    public static void sort_Clusters_Execution_Time(ArrayList<Cluster> cluster) {
        Collections.sort(cluster, new SortByClusterExecutionTime());
    }
    public static ArrayList<Job> sort_Jobs_AccordingToCluster_Ascending(ArrayList<Job> j) {
        if (ContainerPinsSelection.sort_Clusters_According_To.equals("id")) {
            Collections.sort(j, new SortByClusterId());
            return j;
        }
        ArrayList<Cluster> clusters = getUniqueClusters(j);
        initialize_execution_time_total_jobs_of_clusters(clusters, j);
        if (ContainerPinsSelection.sort_Clusters_According_To.equals("job_count")) {
            sort_Clusters_Job_Count(clusters);
        }
        if (ContainerPinsSelection.sort_Clusters_According_To.equals("execution_time")) {
            sort_Clusters_Execution_Time(clusters);
        }
        ArrayList<Job> sorted_jobs = new ArrayList<Job>();
        for (int a = 0; a < clusters.size(); a++) {
            Cluster c = clusters.get(a);
            for (int b = 0; b < j.size(); b++) {
                Job x = j.get(b);
                {
                    if (x.cluster_id == c.Cluster_id) {
                        sorted_jobs.add(x);
                    }
                }
            }
        }
        if (sorted_jobs.size() == j.size()) {
            j = sorted_jobs;
        } else {
            System.out.println("Error in sorting clusters");
        }
        return j;
    }
    public static void update_Job_PM_Id(Job_Generator job_gen, int static_pins, Job j) {
        ArrayList<Job> current_job;
        if (ContainerPinsSelection.clustering == true) {
            if (static_pins == 1) {
                current_job = job_gen.jobs_1;
            } else if (static_pins == 2) {
                current_job = job_gen.jobs_2;
            } else if (static_pins == 3) {
                current_job = job_gen.jobs_3;
            } else if (static_pins == 4) {
                current_job = job_gen.jobs_4;
            } else {
                current_job = job_gen.jobs_p;
            }
        } else {
            current_job = job_gen.jobs;
        }

        for (int i = 0; i < current_job.size(); i++) {
            if (current_job.get(i).job_id == j.job_id) {
                current_job.get(i).PM = j.PM;
                break;
            }
        }
        if (ContainerPinsSelection.clustering == true) {
            if (static_pins == 1) {
                job_gen.jobs_1 = current_job;
            } else if (static_pins == 2) {
                job_gen.jobs_2 = current_job;
            } else if (static_pins == 3) {
                job_gen.jobs_3 = current_job;
            } else if (static_pins == 4) {
                job_gen.jobs_4 = current_job;
            } else {
                job_gen.jobs_p = current_job;
            }
        } else {
            job_gen.jobs = current_job;
        }
    }
    public static void sort_data_integer_ascending(int[] data, int start, int end) {
        int index;
        int temp;
        for (int i = start; i < end - 1; i++) {
            index = i;
            for (int j = (i + 1); j < end; j++) {
                if (data[i] > data[j]) {
                    index = j;
                }
            }
            temp = data[index];
            data[index] = data[i];
            data[i] = temp;
        }
    }
    public static int simple_migration(Job_Generator job_gen, int static_pins, int numberOfJobs, PhysicalMachine[] pm, int current_time) {
        if (ContainerPinsSelection.migration_WRT.equals("heuristic_Free_PM")) {
            return simple_migration_Full_PM(job_gen, static_pins, numberOfJobs, pm, current_time);
        }
        return 0;
    }
    public static int simple_migration_Full_PM(Job_Generator job_gen, int static_pins, int numberOfJobs, PhysicalMachine[] pm, int current_time) {
        if(mig_using_usedCapacity_new == true)
        {
            return simple_migration_Full_PM_UsedCapacity_new(job_gen, static_pins, numberOfJobs, pm, current_time);
        }
        return 0;
    }
    public static int simple_migration_Full_PM_UsedCapacity_new(Job_Generator job_gen, int static_pins, int numberOfJobs, PhysicalMachine[] pm, int current_time) {
        int count = 0;
        int remaining_time = 0;
        PhysicalMachine[] temp_PMs = copyPMSimpleArray(pm);
        ArrayList<Job> temp_Jobs = copyJobsArrayList(job_gen.jobs_p);   
        PhysicalMachine [] temp_copy_PMs = copyPMSimpleArray(pm);
        ArrayList<Job> temp_copy_Jobs = copyJobsArrayList(job_gen.jobs_p);
        int mig = 0;
        sort_PMs_UsedCapcity_Ascending_Range(temp_PMs, 0, temp_PMs.length);
        for (int x = 0; x < (temp_PMs.length - 1); x++) {
            PhysicalMachine x_PM = temp_PMs[x];
            int x_PM_jobs = x_PM.current_jobs.size();
            count = 0;
            for (int j = 0; j < x_PM.current_jobs.size(); j++) {
                Job current_job = x_PM.current_jobs.get(j);
                remaining_time = current_job.end_time - current_time;
                if (remaining_time <= ContainerPinsSelection.threshold_Timer_For_Job_Migration) {
                    break;
                }
                sort_PMs_FreeCapcity_Ascending_Range(temp_PMs, x + 1, temp_PMs.length);
                for (int y = x + 1; y < temp_PMs.length; y++) {
                    PhysicalMachine y_PM = temp_PMs[y];
                    if(y_PM.used_capacity == 0)
                    {
                        continue;
                    }
                    if ((y_PM.used_capacity + current_job.allocated_pins) <= y_PM.max_capacity) {
                        y_PM.used_capacity += current_job.allocated_pins;
                        y_PM.current_jobs.add(current_job);
                        current_job.PM = y_PM.pm_id;
                        temp_PMs[y] = y_PM;
                        int job_index = Cloud.find_Job(temp_Jobs, current_job);
                        temp_Jobs.remove(job_index);
                        temp_Jobs.add(job_index, current_job);
                        x_PM.used_capacity -= current_job.allocated_pins;
                        x_PM.current_jobs.remove(j);
                        temp_PMs[x] = x_PM;
                        count++;
                        j--;
                        break;
                    }
                }
            }
            if(count == x_PM_jobs)
            {
                x_PM.used_capacity = 0;
                x_PM.current_jobs.clear();
                temp_PMs[x] = x_PM;
                temp_copy_PMs = ContainerPinsSelection.copyPMSimpleArray(temp_PMs);
                temp_copy_Jobs = ContainerPinsSelection.copyJobsArrayList(temp_Jobs);
                mig = mig + count;
                ContainerPinsSelection.sort_PMs_UsedCapcity_Ascending_Range(temp_PMs, x + 1, temp_PMs.length);
            }
            else
            {
                copyPMSimpleArray_same_ref(pm, temp_copy_PMs);
                job_gen.jobs_p = temp_copy_Jobs;
                return mig;
            }
        }
        copyPMSimpleArray_same_ref(pm, temp_copy_PMs);
        job_gen.jobs_p = temp_copy_Jobs;
        return mig;
    }
    public static int getFreePMs(PhysicalMachine[] pm) {
        int free = 0;
        for (int i = 0; i < pm.length; i++) {
            if (pm[i].used_capacity == 0) {
                free++;
            }
        }
        return free;
    }
    public static void updateFreePMsData(PhysicalMachine[] pm, ArrayList<Integer> free_PM_count) {
        int temp;
        for (int i = 0; i < number_Of_PMs; i++) {
            if (pm[i].used_capacity == 0) {
                temp = free_PM_count.get(i);
                temp++;
                free_PM_count.remove((int) i);
                free_PM_count.add(i, (Integer) temp);
            }
        }
    }
    public static void updateFreeCoresData(PhysicalMachine[] pm, ArrayList<Integer> free_Cores_count) {
        int temp1, temp2;
        for (int i = 0; i < number_Of_PMs; i++) {
            if(pm[i].used_capacity == 0)
                continue;
            temp1 = free_Cores_count.get(i);
            temp2 = pm[i].max_capacity - pm[i].used_capacity;

            free_Cores_count.remove((int) i);
            free_Cores_count.add(i, (Integer) (temp1 + temp2));
        }
    }
    public static int getJobIndex_In_PM(PhysicalMachine pm, Job j) {
        for (int i = 0; i < pm.current_jobs.size(); i++) {
            if (pm.current_jobs.get(i).job_id == j.job_id) {
                return i;
            }
        }
        return -1;
    }
    public static int getPM_Index(PhysicalMachine[] pm, int id) {
        for (int i = 0; i < pm.length; i++) {
            if (pm[i].pm_id == id) {
                return i;
            }
        }
        return -1;
    }
    public static void AllOthers(int[] numberOfJobsArray) throws Exception {
        int result_size = numberOfJobsArray.length;
        Result[] resultSJF_proposed = new Result[result_size];
        Result[] resultLJF_proposed = new Result[result_size];
        Result[] resultFCFS_proposed = new Result[result_size];
        int count = 0;
        while (count < numberOfJobsArray.length) {
            int numberOfJobs = numberOfJobsArray[count];
            System.out.println("Number of Jobs   \t" + numberOfJobs);
            System.out.println();
            Job_Generator job_gen = new Job_Generator(jobs_file_name + numberOfJobs + ".csv", true);
            System.out.println("Ideal vs Expectedl time");
            System.out.print("jobID\ttype\tsize\tPins_allocated");
            System.out.println("\tExpected\tIdeal");
            for (int j = 0; j < numberOfJobs; j++) {
                System.out.print(j + "\t" + job_gen.jobs.get(j).type + "\t" + job_gen.jobs.get(j).dataset_size);
                System.out.println("\t" + 1 + "\t" + job_gen.jobs.get(j).pin1_time + "\t" + job_gen.jobs.get(j).pin1_ideal_time);
                System.out.print(j + "\t" + job_gen.jobs.get(j).type + "\t" + job_gen.jobs.get(j).dataset_size);
                System.out.println("\t" + 2 + "\t" + job_gen.jobs.get(j).pin2_time + "\t" + job_gen.jobs.get(j).pin2_ideal_time);
                System.out.print(j + "\t" + job_gen.jobs.get(j).type + "\t" + job_gen.jobs.get(j).dataset_size);
                System.out.println("\t" + 3 + "\t" + job_gen.jobs.get(j).pin3_time + "\t" + job_gen.jobs.get(j).pin3_ideal_time);
                System.out.print(j + "\t" + job_gen.jobs.get(j).type + "\t" + job_gen.jobs.get(j).dataset_size);
                System.out.println("\t" + 4 + "\t" + job_gen.jobs.get(j).pin4_time + "\t" + job_gen.jobs.get(j).pin4_ideal_time);
            }
            PhysicalMachine[] pm = new PhysicalMachine[ContainerPinsSelection.number_Of_PMs];
            if(ContainerPinsSelection.PMs_Hetro == true)
            {
                int[] data = getPhysicalMachinesCores(numberOfJobs);
                if(data == null)
                {
                    System.out.println("Error occurred getting PMs cores");
                    break;
                }
                else
                {
                    for (int i = 0; i < data.length; i++) {
                        pm[i] = new PhysicalMachine(i, data[i]);
                    }
                }
            }
            else
            {
                for (int i = 0; i < pm.length; i++) {
                    pm[i] = new PhysicalMachine(i, ContainerPinsSelection.cores);
                }
            }
            System.out.println("FCFS");
            if(ContainerPinsSelection.FCFS == true)
            {
                System.out.println("Proposed_Allocation\t");
                resultFCFS_proposed[count] = execute_jobs_proposed_FCFS_MultiPMs(job_gen, numberOfJobs, pm);
                //pm.reset_pm();
                for (int x = 0; x < pm.length; x++) {
                    pm[x].reset_pm();
                }
                job_gen.reset_jobs();
            }
            System.out.println("SJF");
            if(ContainerPinsSelection.SJF == true)
            {
                System.out.println("Proposed_Allocation\t");
                resultSJF_proposed[count] = execute_jobs_proposed_SJF_MultiPMs(job_gen, numberOfJobs, pm);
                for (int x = 0; x < pm.length; x++) {
                    pm[x].reset_pm();
                }
                job_gen.reset_jobs();
                System.out.println();
            }
            System.out.println("LJF");
            if(ContainerPinsSelection.LJF == true)
            {
                System.out.println("Proposed_Allocation\t");
                resultLJF_proposed[count] = execute_jobs_proposed_LJF_MultiPMs(job_gen, numberOfJobs, pm);
                for (int x = 0; x < pm.length; x++) {
                    pm[x].reset_pm();
                }
                job_gen.reset_jobs();
                System.out.println();
            }
            count++;
        }
        display_Results("SJF", resultSJF_proposed);
        display_Results("LJF", resultLJF_proposed);
        display_Results("FCFS",resultFCFS_proposed);
       
    }
    public static void display_Results_Vertical(String algo_Name, Result[]res) {
        System.out.println("Migration: " + ContainerPinsSelection.count_Migrations);
        System.out.println("Migration Timer: " + ContainerPinsSelection.timer_Count_Migrations);
        System.out.println("Migration WRT: " + ContainerPinsSelection.migration_WRT);
        System.out.println("Clustering: " + ContainerPinsSelection.clustering);
        System.out.println("Offloading: " + ContainerPinsSelection.isOffloading);
        System.out.println("Pre-Offloading: " + ContainerPinsSelection.pre_Offloading);
        System.out.println("Mig in cloud PMs: " + ContainerPinsSelection.count_Migrations_In_Cloud);
        System.out.println("Mig from cloud to fog: " + ContainerPinsSelection.enable_mig_cloud_to_local_FCFS);
        System.out.println("Hetrogeneous Fog Nodes (PMs): " + ContainerPinsSelection.PMs_Hetro);
        System.out.println("PMs: " + ContainerPinsSelection.number_Of_PMs);
        System.out.println("\tResult Summary " + algo_Name);
        System.out.println("\t\tTotal Time " + algo_Name + "\t Average Waiting Time " + algo_Name + "\t Average Waste Capacity " + algo_Name + "\t Time Intervals (Any Free PM) " + algo_Name + "\t Average Number Of Free PMs per Interval " + algo_Name + "\t Average Number Of Free PMs New " + algo_Name + "\t Average Number Of Free Cores per Interval " + algo_Name + "\t Total Number of Migrations " + algo_Name + "\t Cloud Used " + algo_Name + "\t Total Number of PMs in Cloud " + algo_Name + "\t Total Time Interval For Cloud " + algo_Name + "\t Total PMs on Cloud " + algo_Name + "\t Migration Cloud to Local " + algo_Name + "\t Migrations in Cloud " + algo_Name + "\t Waste Cores in Cloud " + algo_Name);
        String[] y_labels = new String[]{"static_1", "static_2", "static_3", "static_4", "proposed"};
        for (int i = 0; i < ContainerPinsSelection.numberOfJobsArray.length; i++) {
            System.out.print(ContainerPinsSelection.numberOfJobsArray[i] + "\t");
            if(res[i] == null)
                continue;
            System.out.print(res[i].totalTime + "\t");
            System.out.print(res[i].averageWaitTime + "\t");
            System.out.print(res[i].averageWasteCapacity + "\t");
            System.out.print(res[i].averageFreePMsPercentage + "\t");
            System.out.print(res[i].avergaeNumberOfMigrations + "\t");
            System.out.print(res[i].totalTimeForCloud + "\t");
            System.out.print(res[i].totalPM_On_Cloud + "\t");
            System.out.print(res[i].local_migrations_from_cloud_to_local + "\t");
            System.out.print(res[i].total_Migrations_In_Cloud + "\t");
            System.out.println();
        }
    }
    public static void display_Results(String algo_Name, Result[]res) {
        if (ContainerPinsSelection.display_Form.equals("vertical")) {
            ContainerPinsSelection.display_Results_Vertical(algo_Name, res);
            return;
        }
    }
    private static int[] getPhysicalMachinesCores(int numberOfJobs)throws Exception{
        BufferedReader as = new BufferedReader(new FileReader(PMs_cores_file_name + ".csv"));
        String line = as.readLine();
        int [] cores = null;
        while (line != null) {
            String [] file_cores = line.split(",");
            if(Integer.parseInt(file_cores[0]) == numberOfJobs){
                cores = new int [file_cores.length - 1];
                for(int i = 0; i < file_cores.length - 1; i++){
                    cores[i] = Integer.parseInt(file_cores[i+1]);
                }
                break;
            }
            line = as.readLine();
        }
        as.close();
        return cores;
    }
    public static void AllOthers_Clustering(int[] numberOfJobsArray) throws Exception {
        int result_size = numberOfJobsArray.length;
        Result[] resultSJF_proposed = new Result[result_size];
        Result[] resultLJF_proposed = new Result[result_size];
        Result[] resultFCFS_proposed = new Result[result_size];
        int count = 0;
        while (count < numberOfJobsArray.length) {
            int numberOfJobs = numberOfJobsArray[count];
            System.out.println("Number of Jobs   \t" + numberOfJobs);
            System.out.println();
            Job_Generator job_gen = new Job_Generator();
            PhysicalMachine[] pm = new PhysicalMachine[ContainerPinsSelection.number_Of_PMs];
            if(ContainerPinsSelection.PMs_Hetro == true)
            {
                int[] data = getPhysicalMachinesCores(numberOfJobs);
                if(data == null)
                {
                    System.out.println("Error occurred getting PMs cores");
                    break;
                }
                else
                {
                    for (int i = 0; i < data.length; i++) {
                        pm[i] = new PhysicalMachine(i, data[i]);
                    }
                }
            }
            else
            {
                for (int i = 0; i < pm.length; i++) {
                    pm[i] = new PhysicalMachine(i, ContainerPinsSelection.cores);
                }
            }
            System.out.println("FCFS");
            if(ContainerPinsSelection.FCFS == true)
            {
                job_gen.jobs_p = getJobsForProposedPins(jobs_file_name + numberOfJobs + ".csv", ContainerPinsSelection.threshold_For_Proposed_FCFS, (numberOfJobs * 5) + 1, numberOfJobs);
                job_gen.jobs_p = sort_Jobs_AccordingToCluster_Ascending(job_gen.jobs_p);
                resultFCFS_proposed[count] = execute_jobs_proposed_FCFS_MultiPMs(job_gen, numberOfJobs, pm);
                for (int x = 0; x < pm.length; x++) {
                    pm[x].reset_pm();
                }
            }
            job_gen.reset_jobs(-1);
            job_gen.jobs_p = sort_Jobs_AccordingToCluster_Ascending(job_gen.jobs_p);
            System.out.println("SJF");
            if(ContainerPinsSelection.SJF == true)
            {
                job_gen.jobs_p = getJobsForProposedPins(jobs_file_name + numberOfJobs + ".csv", ContainerPinsSelection.threshold_For_Proposed_SJF, (numberOfJobs * 5) + 1, numberOfJobs);
                job_gen.jobs_p = sort_Jobs_AccordingToCluster_Ascending(job_gen.jobs_p);
                resultSJF_proposed[count] = execute_jobs_proposed_SJF_MultiPMs(job_gen, numberOfJobs, pm);
                for (int x = 0; x < pm.length; x++) {
                    pm[x].reset_pm();
                }
            }
            job_gen.reset_jobs(-1);
            job_gen.jobs_p = sort_Jobs_AccordingToCluster_Ascending(job_gen.jobs_p);
            System.out.println("LJF");
            if(ContainerPinsSelection.LJF == true)
            {
                job_gen.jobs_p = getJobsForProposedPins(jobs_file_name + numberOfJobs + ".csv", ContainerPinsSelection.threshold_For_Proposed_LJF, (numberOfJobs * 5) + 1, numberOfJobs);
                job_gen.jobs_p = sort_Jobs_AccordingToCluster_Ascending(job_gen.jobs_p);
                resultLJF_proposed[count] = execute_jobs_proposed_LJF_MultiPMs(job_gen, numberOfJobs, pm);
                for (int x = 0; x < pm.length; x++) {
                    pm[x].reset_pm();
                }
            }
            job_gen.reset_jobs(-1);
            job_gen.jobs_p = sort_Jobs_AccordingToCluster_Ascending(job_gen.jobs_p);
            count++;
        }
        display_Results("SJF", resultSJF_proposed);
        display_Results("LJF", resultLJF_proposed);
        display_Results("FCFS", resultFCFS_proposed);
    }
    public static ArrayList<Job> getJobsForProposedPins(String fileName, double threshold, int index_starts, int numberOfJobs) throws Exception {
        if(ContainerPinsSelection.isOffloading == true)
        {
            return getJobsForProposedPins_Offloading(fileName, index_starts, numberOfJobs);
        }
        ArrayList<Job> collect_jobs = new ArrayList<Job>();
        Scanner s = new Scanner(System.in);
        BufferedReader as = new BufferedReader(new FileReader(fileName));
        String line = as.readLine();
        int i = index_starts;
        while (line != null) {
            int size = Integer.parseInt(line.split(",")[1]);
            int type = Integer.parseInt(line.split(",")[0]);
            int time4 = Integer.parseInt(line.split(",")[2]);
            int time3 = Integer.parseInt(line.split(",")[3]);
            int time2 = Integer.parseInt(line.split(",")[4]);
            int time1 = Integer.parseInt(line.split(",")[5]);
            Job job = new Job(i, type, size, time1, time2, time3, time4, -1);
            double minOverhead = Min(job.overhead2, job.overhead3, job.overhead4);
            if (minOverhead > threshold) {
                job.allocated_pins = 1;
                job.executionTime = job.pin1_time;
            } else if (minOverhead == job.overhead2) {
                job.allocated_pins = 2;
                job.executionTime = job.pin2_time;
            } else if (minOverhead == job.overhead3) {
                job.allocated_pins = 3;
                job.executionTime = job.pin3_time;
            } else {
                job.allocated_pins = 4;
                job.executionTime = job.pin4_time;
            }
            collect_jobs.add(job);
            i++;
            line = as.readLine();
        }
        as.close();
        if (ContainerPinsSelection.is_New_Cluster_For_Proposed == false) {
            as = new BufferedReader(new FileReader(cluster_file_name + numberOfJobs + ".csv"));
            line = as.readLine();
            int j = 0;
            while (line != null) {
                int cluster_id = Integer.parseInt(line.split(",")[2]);
                Job x = collect_jobs.get(j);
                x.cluster_id = cluster_id;
                line = as.readLine();
                j++;
            }
            return collect_jobs;
        }
        PrintWriter pw = new PrintWriter(new FileWriter(cluster_file_name + numberOfJobs + ".csv"), false);
        String str;
        for (int j = 0; j < collect_jobs.size(); j++) {
            Job x = collect_jobs.get(j);
            str = x.executionTime + "\t" + x.allocated_pins;
            pw.write(str + "\n");
        }
        pw.flush();
        System.out.println("cluster_file.csv is done, Kindly update cluster ids");
        System.out.println("y/n");
        if (s.nextLine().equals("y")) {
            System.out.println("Okay");
        }
        as = new BufferedReader(new FileReader(cluster_file_name + numberOfJobs + ".csv"));
        line = as.readLine();
        int j = 0;
        while (line != null) {
            int cluster_id = Integer.parseInt(line.split(",")[2]);
            Job x = collect_jobs.get(j);
            x.cluster_id = cluster_id;
            line = as.readLine();
            j++;
        }
        return collect_jobs;
    }
    public static ArrayList<Job> getJobsForProposedPins_Offloading(String fileName, int index_starts, int numberOfJobs) throws Exception {
        ArrayList<Job> collect_jobs = new ArrayList<Job>();
        Scanner s = new Scanner(System.in);
        
        BufferedReader as = new BufferedReader(new FileReader(fileName));
        String line = as.readLine();
        int i = index_starts;

        while (line != null) {
            int type = tuple_Id(line.split(",")[0]);
            int size = Integer.parseInt(line.split(",")[1]);
            
            int time4 = Integer.parseInt(line.split(",")[2]);
            int time3 = Integer.parseInt(line.split(",")[2]);
            int time2 = Integer.parseInt(line.split(",")[2]);
            int time1 = Integer.parseInt(line.split(",")[2]);            
            int pins = Integer.parseInt(line.split(",")[3]);
            int cluster_id = Integer.parseInt(line.split(",")[4]);
            Job job = new Job(i, type, size, time1, time2, time3, time4, -1);
            job.allocated_pins = pins;
            job.executionTime = job.pin1_time;
            job.cluster_id = cluster_id;
            collect_jobs.add(job);
            i++;
            line = as.readLine();
        }
        as.close();
        return collect_jobs;
    }
    public static void main(String[] args) throws Exception {
        if (ContainerPinsSelection.clustering == true) {
            ContainerPinsSelection.AllOthers_Clustering(ContainerPinsSelection.numberOfJobsArray);
        } else {
            AllOthers(ContainerPinsSelection.numberOfJobsArray);
        }
    }
}