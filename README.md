# Lab 1 - Build (Pre-Completed - Reference Only)

> **Note:** This lab has been pre-completed for you. We will walk through the configuration during the workshop introduction, but you will not need to create these steps. This pipeline is already set up and ready to use in subsequent labs.

## Summary
Setup a CI Pipeline, including running source code tests, building the executable, building and pushing the artifact to a remote repository

### Outcome
A Deployable artifact

### Learning Objective(s):

- Understand how to configure a basic pipeline using Harness CI

- Review how to build and deploy an artifact to a remote repository using Harness CI

- Understand how unit tests are integrated into the build process using Harness CI

## Reference Steps

1. From the left hand menu, navigate to **Projects** → **Select the project available**

    ![](https://lh7-us.googleusercontent.com/docsz/AD_4nXfhuMykMsIHl-7FjliWssHc0uwRpdLdrnq7GkGAI0g6UBZM69F1zpQ8ZA8N_vMqjpoGFYFR_weJk7OtOGGa2bksIaS6BlktwytmuJ1THM3e8O6tDT18HYWwFyGUye8ubsrHBChI8ORrCQ88JcKWpLjQ0DsXDS0NSZrkfZ4RUQ?key=cRG2cvp_PHVW0KG2Gq6Y_A)

2. From the left hand side menu select **Pipelines**

3. Click **+ Create a Pipeline**, enter the following values, then click **Start**

    | Field | Value | Notes |
    | ----  | ----- | ----- |
    | Name | workshop | *This is the name of the pipeline* |
    | How do you want to setup your pipeline? | Inline | *This indicates that Harness (rather than Git) will be the source of truth for the pipeline* |

4. From Pipeline Studio, Click **Add Stage** and select **Build** as the Stage Type

5. Enter the following values and click on **Set Up Stage**

    | Input | Value | Notes |
    | ----  | ----- | ----- |
    | Stage Name | Build | *This is the name of the stage* |
    | Clone Codebase | Enabled | *This indicates that the codebase will be cloned* |
    | Repository Name | harnessrepo | *This is the name of the repository* |

6. There are **two** main tabs that need configuration:
    ### Infrastructure

   | Input | Value | Notes |
   | ----  | ----- | ----- |
   | Infrastructure | Cloud | *Harness Cloud provides managed build infrastructure on demand* |

    ### Execution

   - Select **Add Step**, then **Add Step** again, then select **Test Intelligence** from the Step Library and configure with the following

   | Input | Value | Notes |
   | ----  | ----- | ----- |
   | Name | Run Tests With Intelligence | *Test Intelligence speeds up test execution by running only the tests that are relevant to the changes made in the codebase.* |
   | Command | pip install pytest & cd ./python-tests | *The github repo is a monorepo with application(s) and configuration in the same repo. Therefore we need to navigate to the application subfolder* |

   - After completing configuration select **Apply Changes** from the top right of the configuration popup

   - Select **Add Step**, then **Use template** (In this step we will be building the binary following same config as before. To avoid duplication of efforts a template has been precreated)

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Template Name | Compile Application | *This template provides us a reusable and standard way to build Angular applications* |

   - Select the template and press **Use Template,** then provide a name for that template

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name  | Compile | *Name of the template in the pipeline* |

   - Select **Add Step**, then **Add Step** again, then select **Build and Push an image to Docker Registry** from the Step Library and configure with the following

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name  |Push to DockerHub | |
   | Docker Connector | dockerhub | |
   | Docker Repository | nikpap/harness-workshop | |
   | Tags | <+variable.username>-<+pipeline.sequenceId> | *This will be the tag of the image using harness expressions. Click on the pin and select expression and paste the value* |
   | **Optional Configuration** | | |
   | Dockerfile | /harness/frontend-app/harness-webapp/Dockerfile |  *This tells harness where is the Dockerfile for building the app* |
   | Context | /harness/frontend-app/harness-webapp | *This tells from where to run the instructions included in the dockerfile* |

   - Click **Apply Changes** to close the config dialog

  7. Click **Save** and then click **Run** to execute the pipeline with the following inputs

     | Input | Value | Notes |
     | ----- | ----- | ----- |
     | Branch Name | main | *This is prepopulated* |

# Lab 2 - DevSecOps (Pre-Completed - Reference Only)

> **Note:** This lab has been pre-completed for you. We will walk through the security scanning configuration during the workshop introduction, but you will not need to create these steps. The security scans are already integrated into your pipeline.

## Summary:
Our security team has implemented orchestration of **Fortify** and **OWASP** scans for our code in a reusable form **(templates)**. In order to improve our security posture they have also added policies to enforce us to include those templates

### Learning Objective(s):

- Understand how governance plays a role in the path to production

- See how reusable templates make developer's life easier

- Understand how DevSecOps practices can be easily achieved

![](https://lh7-us.googleusercontent.com/docsz/AD_4nXcLr5TGcKRWOjVgB_sCAHHEeLPyd6EBdnkt2-mq_imTkZbQMEwJD03Q1wZyhWqHxoCNIIYWJWlRbnZrvZn2pPYIwTzXlOGdhMDEgn-J2JnK7lVastmfpdwTqDHXjpP0DK3TgU1gM-Ec_0iZLicWV7KpgW2FdXUCcAtraDGaEz8hI3dpWGLXkg?key=cRG2cvp_PHVW0KG2Gq6Y_A)

## Reference Steps

1. In the existing pipeline, within the Build stage **before** PushToDockerhub step click on the plus icon to add a new step

2. Select use template

   ![](https://lh7-us.googleusercontent.com/docsz/AD_4nXeC5rTVxlk7DeZeU_cINwcKo6Nf2wVW9brQ9MiCEfppJwmU-uH3QcNZ53qTxhur57KeySksoDBg9EqjhgKOgAEDKon6iNz9cFxozBe9VZssV-t77VNo6t1zPUvm6e2NOZJDKncxd9c2GM4HE-h-L4cIOl4u6Uqx_azoKchMdg?key=cRG2cvp_PHVW0KG2Gq6Y_A)

3. Select **DevX Fortify Scan** 

4. Name the step **Fortify**

5. In the existing pipeline, within the Build stage **after** PushToDockerhub step click on the plus icon to add a new step

6. Select use template

7. Select **OWASP**

8. Name the step **OWASP**

9. Click **Save** and then click **Run** to execute the pipeline with the following inputs

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Branch Name | main | |

After the **Build and Push** stage is complete, go to the **Security Tests** tab to see the deduplicated, normalized and prioritized list of vulnerabilities discovered across your scanners.

# Lab 3 - Continuous Deploy - Frontend

## Summary: 
Extend your existing pipeline to take the artifact built in the CI/Build stage and deploy it to an environment

### Learning Objective(s):

- Add a second stage to an existing pipeline

- Create a k8s service

- Incorporate an advanced deployment strategy such as Canary

- Create custom Harness variables

- Create an Input Set

## Steps

1. In the existing pipeline, add a Deployment stage by clicking **Add Stage** and select **Deploy** as the Stage Type

2. Enter the following values and click on **Set Up Stage**

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Stage Name | frontend | |
   | Deployment Type | Kubernetes | |

3. Configure the **frontend** Stage with the following
   
   ### Service
   
   - Click **+Add Service** and configure as follows

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | frontend | |
   | Deployment Type | Kubernetes | |
   | **Add Manifest** | | |
   | Manifest Type | K8s Manifest | |
   | K8s Manifest Store | Code | |
   | Manifest Identifier | templates | |
   | Repository | harnessrepo | |
   | Branch | main | |
   | File/Folder Path | harness-deploy/frontend/manifests | |
   | Values.yaml | harness-deploy/frontend/values.yaml | |
   | **Add Artifact Source** | | |
   | Artifact Repository Type | Docker Registry | |
   | Docker Registry Connector  |dockerhub | |
   | Artifact Source Identifier |frontend | |
   | Image Path | nikpap/harness-workshop |                                    |
   | Tag | <+variable.username>-<+pipeline.sequenceId> | *Select value, then click on the pin and select expression and paste the value* |

   - Click **Save** to close the service window and then click **Continue** to go to the Environment tab

   ### Environment
   The target infrastructure has been pre-created for us. The application will be deployed to a GKE cluster on the given namespace  

   - Click **- Select -** on the **"Specify Environment"** input box

   - Select **prod** environment and click **"Apply Selected"**

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | prod | *Make sure to select the environment and infrastructure definition* |

   - Click **- Select -** on the **"Specify Infrastructure"** input box

   -  From the dropdown select GKE

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | GKE | |

   - Click **Continue** 

   ### Execution Strategies

   Select **Rolling** and click on **Use Strategy**, the frontend is a static application so no need to do canary.

# Lab 4 - Continuous Deploy - Backend

## Summary
Extend your existing pipeline to derisk production deployments

### Learning Objective(s):

- Utilise complex deployment strategies to reduce blast radius of a release

## Steps

1. In the existing pipeline, add a Deployment stage by clicking **Add Stage** and select **Deploy** as the Stage Type

2. Enter the following values and click on **Set Up Stage**

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Stage Name | backend | |
   | Deployment Type | Kubernetes | |

3. Configure the **backend** Stage with the following

   ### Service

   - Click **- Select -**  on the **"Select Service"** input box and configure as follows:

      | Input | Value | Notes |
      | ----- | ----- | ----- |
      | Name | backend | |

   - Click **Apply Selected** and then click **Continue** to go to the **"Environment"** tab

   ### Environment
   The target infrastructure has been pre-created for us and we used it in the previous stage. To reuse the same environment

   - Click **- Propagate Environment From**

   - Select **Stage [frontend]**

   - Click **Continue**

   ### Execution

   - Select **Canary** and click on **Use Strategy**

   - **After** the canary deployment and **before** the canary delete step add **Harness Approval** step according to the table below

     | Input       | Value             | Notes |
     | ----------- | ----------------- | ----- |
     | Name        |Approval|       |
     | User Groups |All Project Users|     Select project to see the **"All Project Users"** option   |
- Click **Apply Changes**

4. Click **Save** and then click **Run** to execute the pipeline with the following inputs. As a bonus, save your inputs as an Input Set before executing (see below)

| Input       | Value | Notes       |
| ----------- | ----- | ----------- |
| Branch Name |main| Leave as is |

5. While the canary deployment is ongoing and waiting **approval** navigate to the web page and see if you can spot the canary (use the check release button) 

| Project | Domain | Suffix |
| ------- | ------ | ------ |
| http\://project_id | .cie-bootcamp | .co.uk |

![](https://lh7-us.googleusercontent.com/docsz/AD_4nXfmb1N3lAe0EOnEun9neU9y3ilqy3HbxfnWfUMzF3FsykslwgQfU_W4pE0wlt5kYSp6_mTs7cVP0anhJ7uvtsytal2qX3ZEq3vvOT3DOBUzE9SZ3rpwkAHP6e_ExdRbo5VmN2kpxdFlp6u8iGaKwhW_uyAohEmJurkjmEB2Ww?key=cRG2cvp_PHVW0KG2Gq6Y_A)

6. Approve the canary deployment for the pipeline to complete

# TODO: IaCM Lab
## Summary
### Learning Objective(s):
## Steps

# TODO: Multicloud Lab
## Summary
### Learning Objective(s):
## Steps
remember to include the creation of the infra scope override

# TODO: SNOW Lab
## Summary
### Learning Objective(s):
## Steps

# Lab 5 - Continuous Verification

## Summary
Increase resiliency of applications by embedding chaos experiments into the delivery process and integrating with observability tools through continuous verification

### Learning Objective(s):
- Embed chaos experiments into deployment pipelines to validate canary releases
- Add continuous verification to the deployed service
- Automate release validation

## Steps

1. From the module selection menu select Continuous Delivery & GitOps

   ![Screenshot 2024-11-28 at 14 07 22](https://github.com/user-attachments/assets/898ee27b-7369-47c6-a145-e74b49bb4bed)

   
2. From the left hand side menu select pipelines and drill down to the existing pipeline

3. In the existing pipeline, within the Deploy backend stage **after** Canary Deployment and **before** the approval step click on the plus icon to add a new step

4. Add a **Verify** step with the following configuration

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | Verify | |
   | Continuous Verification Type | Canary | |
   | Sensitivity | High | *This is to define how sensitive the ML algorithms are going to be on deviation from the baseline* |
   | Duration | 5mins | |

5. Under the verify step click on the plus icon to add a new step in parallel

   ![Screenshot 2024-11-28 at 14 28 38](https://github.com/user-attachments/assets/368ba808-d303-43f8-8824-5d2e09367b01)

   
6. Add a **chaos** step with the following configuration

   | Input | Value |
   | ----- | ----- |
   | Name | Chaos |
   | Select Chaos Experiment | <project_name>-pod-memory |
   | Expected Resilience Score | 50 | 

7. Click on Apply Changes

8. Click **Save**

# Lab 6 - Release Validation & Automatic Rollback

## Summary
Validate release using Continuous Verification

### Learning Objective(s):
- Use complex deployment strategies to reduce the blast radius
- Add continuous verification to the deployed service
- Automate release validation
- Rollback unstable releases

### Outcomes
- Force failure of continuous delivery validation using chaos engineering

## Steps

-----------
1. **TODO** write the steps to update the backend service to "backend-v2"
-----------

2. Click **Run** to execute the pipeline with the following inputs. As a bonus, save your inputs as an Input Set before executing (see below)

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Branch Name | main | *Leave as is* |

3. While the canary deployment is ongoing navigate to the web page and see if you can spot the canary (use the check release button) 

   | project                | domain        | suffix |
   | ---------------------- | ------------- | ------ |
   | http\://\<project\_id> | .cie-bootcamp | .co.uk |

- Validate that we've deployed the new version in the canary by checking the version is **backend-v2** and the Last Execution matches the **build Id** of your pipeline

------
**TODO** insert screenshot of canary with v2 and where to find the build Id
------

- Drill down to the distribution test tab and run the traffic generation by clicking the **Start** button

- Observe the traffic distribution

- Validate the outcome of the verification on the pipeline execution details

![](https://lh7-us.googleusercontent.com/docsz/AD_4nXdbAmEJ5zQPsKlw_nEknWvYo97pm5eWCXr6vU8-GgIL0ulAOSH9N07PoEcVSknARVQo7Tgj1s31VHqR1I3hu2dMIO1rIX5HHcmTPXoQPoyo8CPv13OhnJN5WVcZqSwUXzdDHmm3PxUnhtpGVl0PAMJ_1wnuodvUbVPBOdnGKQ?key=cRG2cvp_PHVW0KG2Gq6Y_A)
![](https://lh7-us.googleusercontent.com/docsz/AD_4nXf-5oWX9OfvdmEb9MBm2_h2KKAa_QwmiJoM0fiKrTuxAr6GR4wxeulSlk48gyBK3dykrtIslDSkxpiGytrxH0JaxaQ4ZgTYxbmc8OenAH3nhGCvvOAxkWVjVBp1TRg_qQQi9z8OrNPK4udPtNL1LIyym6Ch5IMzrulFOcXhOQ?key=cRG2cvp_PHVW0KG2Gq6Y_A)

**Bonus**:
- If the verification fails harness defaults to a manual intervention, you can now decide what you want to happen next (rollback, ignore etc.) 

- Add a canary rollout from 10% to 50% traffic and see how this impacts the traffic distribution


# Lab 7 - Governance/Policy as Code

### Summary
Create and apply policies as code in order to enable governance and promote self-service. In Lab 2 we saw how a user is impacted by policies in place, now is the time to create such policies

### Learning Objective(s):

- Create a policy that evaluates when editing pipelines

- Create a policy that evaluates during pipeline execution

- Test policy enforcement

**Steps\
****Create a Policy to require Approvals**

1. From the secondary menu, select **Project Settings** and select **Governance Policies**

2. Click **Build a Sample Policy**

3. From the suggested list select **Pipeline - Approval**  and click on next

4. Click Next: Enforce Policy

5. Set the values according to the table  below and confirm

| Input            | Value        | Notes |
| ---------------- | ------------ | ----- |
| Trigger Event    |On Run|       |
| Failure Strategy |Error & exit|       |

**Test the Policy to require Approvals**

1. Open your pipeline

2. Try to run the pipeline and note that the failure due to lack of an approval stage

3. Click **Save** and note that the failure due to lack of an approval stage

4. Open the pipeline in edit mode and navigate to the “**frontend**” stage

5. Before the rolling deployment step add **Harness Approval** step according to the table  below

| Input            | Value            | Notes |
| ---------------- | ---------------- | ----- |
| Step Name        |Approval|       |
| Type of Approval |Harness Approval|       |

6. Configure the Approval step as follows

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | Approval | |
   | User Groups | All Project Users | |

7. In a similar way as before navigate to the “**backend**” stage

8. Before the canary deployment block add **Harness Approval**

9. Click **Save** and note that the save succeeds without any policy failure


# Lab 8 - Governance/Policy as Code (Advanced)

### Summary
Create advanced policies to block critical CVEs and enforce security standards

### Learning Objective(s):

- Create policies that evaluate security vulnerabilities
- Block deployments with critical CVEs
- Integrate policy enforcement into pipelines

**Steps**

**Create a Policy to block critical CVEs**

1. From the secondary menu, select **Project Settings** and select **Policies**

2. Select the **Policies** tab 

3. click **+ New Policy**, set the name to **Runtime OWASP CVEs** and click **Apply**

4. Set the rego to the following and click **Save**

<!---->

    package pipeline_environment
    deny[sprintf("Node OSS Can't contain any critical vulnerability '%d'", [input.NODE_OSS_CRITICAL_COUNT])] {  
       input.NODE_OSS_CRITICAL_COUNT != 0
    }

5. Select the **Policy Sets** tab

6. Click **+ New Policy Set** and configure as follows

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | Criticals Not Allowed | |
   | Entity Type | Custom | |
   | Event Evaluation | On Step | |
   | Policy Evaluation Criteria | | |
   | Policy to Evaluate | Runtime OWASP CVEs | |

7. For the new policy set, toggle the **Enforced** button

**Add Policy to Pipeline**

1. Open your pipeline

2. Go to an execution that already ran, and copy the CRITICAL output variable from the OWASP step like so:\
   ![](https://lh7-us.googleusercontent.com/docsz/AD_4nXfYQ7ba5Q_cQ9xy2AFVZ5Mt0iZPYbyQDmBonp0pBQA13Z_IUeYdK8gRSbddtf_V3bSRfbhKWDbRSUVJTx3BTCc_VmwLIWyWLkdh89nLh0sEBA6fqQxTy0NADZ0YPZwCirNycRVGUQACdItaBotovPs5Hg6CmRpQHk5ysgV6RUlhSbIbkNxmHAo?key=cRG2cvp_PHVW0KG2Gq6Y_A)

3. Select the **frontend** stage

4. Before the **Rollout Deployment** Step Group, add a **Policy** type step and configure as follow

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | Policy - No Critical CVEs | |
   | Entity Type | Custom | |
   | Policy Set | Criticals Now Allowed | *Make sure to select the Project tab in order to see your Policy Set* |
   | Payload | {"NODE_OSS_CRITICAL_COUNT": _\<variable>_} | *Set the field type to Expression, then replace _\<variable>_ with OWASP output variable CRITICAL. Go to a previous execution to copy the variable path.* |

5. Save the pipeline and execute. Note that the pipeline fails at the policy evaluation step due to critical vulnerabilities being found by OWASP.
