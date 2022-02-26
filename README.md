# Two-way-Offloading-For-Efficient-Job-Placement-Using-Fog-Cloud-Architecture-Simulation

# Container Pin Selection
  In ContainerPinSelection, all the data members are initialized to execute the simulation accordingly.

    boolean count_Migrations = true/false;      //Conditional migration in fog data center
    boolean count_Migrations_In_Cloud = true;   //Conditional migration in cloud data center
    String cluster_file_name;                   //files having clusters
    String PMs_cores_file_name;                 //files having number of cores for heterogeneous PMs in fog data center
    int number_Of_PMs;                          //number of PMs in fog data center
    int cores;                                  //number of cores for homogeneous PMs in fog data center
    int[] numberOfJobsArray;                    //number of jobs in each batch
    boolean clustering;                         //enable clustering techniquq
    boolean isOffloading;                       //enable offloading technique
    boolean PMs_Hetro;                          //enable heterogeneous PMs
    boolean FCFS = false;                       //enable FCFS
    boolean SJF = true;                         //enable SJF
    boolean LJF = false;                        //enable LJF
    
# Job Batch files
- create a job batch with the name tupleXX.csv (XX is the number of jobs, e.g if batch has 10 jobs, then name of the file is tuple10.csv)
- all jobs are created with 5 attributes (job type, job size, job execution time, required CPU pins, and job cluster id)

# experments result details

This project run the iteration of each algorithm, and output the following values for each iteration against all discussed algorithms.

    completion time : (sec)
    waiting time    : (sec)
    wastage time    : (sec)
    avg free PMs    : (%)
    # of migration  : (count) // in fog data center
    total time      : (sec)   // in cloud data center
    # of PMs        : (count) // in cloud data center
    back offloading : (count)
    # of migration  : (count) // in cloud data center
