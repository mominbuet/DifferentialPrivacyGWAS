# DifferentialPrivacyGWAS

### Database Setup
For the LD, HWE, CATT and FET GWAS, the project will require a database (*i.e.*, MySql) where we will be keeping the genomics data. We generate random synthetic data from the SNP frequency file (*freq_refined.txt*). The sql file for the current results can be found in 'genomic_data.sql' file. 

The code will need a *persistence.xml* file (as it uses Eclipse Persistence) that should contain the database connection parameters: hostname, username and password to the database.

#### Data Generation
To generate new data, use the main java files in **insertion** package. 

#### Global Model
The global and local model both will get their data from a table named *gwas_original_local* (rename it if needed in *DB.GwasPlaintext.java*). The global model is available in **cs.umanitoba.ca.dpbinpacking.GWASDP** as there are four functions that will give the accuracy results:

- calculateLD (TargetSNP)
- calculateHWE (TargetSNP)
- calculateCATT (TargetSNPCase, TargetSNPControl)
- calculateFET (TargetSNPCase, TargetSNPControl)

The epsilon values can be changed in the java file as well. The noise values are added to the statistics all handled from *GWASDP.java*.


#### Local Model
The local model can be executed from **GWASLocalLapDP.java** and **GWASLocalRRDP.java** for the Laplace and Randomized Response method in **cs.umanitoba.ca.dpbinpacking** package. These operate on differet tables in the database which are also included on the **genomic_db.sql** file.

For any queries please email azizmma at cs.umanitoba.ca