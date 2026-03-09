# Table of Contents

- [Lab 1 - Continuous Integration - Build](#user-content-lab-1---continuous-integration---build)
- [Lab 2 - DevSecOps](#user-content-lab-2---devsecops)
- [Lab 3 - Continuous Deployment - Frontend](#user-content-lab-3---continuous-deployment---frontend)
- [Lab 4 - Continuous Deployment - Backend](#user-content-lab-4---continuous-deployment---backend)
- [Lab 5 - Artifact Registry](#user-content-lab-5---artifact-registry)
- [Lab 6 - Policy, Governance & Change Management](#user-content-lab-6---policy-governance--change-management)
- [Lab 7 - Continuous Verification](#user-content-lab-7---continuous-verification)
- [Lab 8 - Release Validation & Automatic Rollback](#user-content-lab-8---release-validation--automatic-rollback)
- [Lab 9 - Automated Security Standards Enforcement](#user-content-lab-9---automated-security-standards-enforcement)
- [Lab 10 - Enhanced Change Management Automation (Optional)](#user-content-lab-10---enhanced-change-management-automation)

---

# Lab 1 - Continuous Integration - Build

## Summary
Every great deployment starts with a great build. In this lab, you'll set up a CI pipeline from scratch: running tests, compiling the application, and pushing a container image to a container registry. Think of it as laying the foundation so everything downstream has something solid to stand on.

## Objectives

- Create a CI pipeline using Harness CI
- Run unit tests using Test Intelligence for faster feedback loops
- Build and push a container image to a container registry
- Understand how reusable templates reduce duplication across pipelines

## Why It Matters
If your artifacts aren't built consistently, nothing downstream matters. This lab establishes the starting point of the software delivery lifecycle and ensures every artifact entering the deployment process is repeatable and traceable. No more "works on my machine" just clean, versioned builds ready for prime time.

## Steps
**1.** From the Unified View left navigation bar, navigate to **Projects** → **Select the project available**

![](https://lh7-us.googleusercontent.com/docsz/AD_4nXfhuMykMsIHl-7FjliWssHc0uwRpdLdrnq7GkGAI0g6UBZM69F1zpQ8ZA8N_vMqjpoGFYFR_weJk7OtOGGa2bksIaS6BlktwytmuJ1THM3e8O6tDT18HYWwFyGUye8ubsrHBChI8ORrCQ88JcKWpLjQ0DsXDS0NSZrkfZ4RUQ?key=cRG2cvp_PHVW0KG2Gq6Y_A)

**2.** From the Unified View left navigation bar select **Pipelines**

**3.** Click **+ Create a Pipeline**, enter the following values, then click **Start**

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | workshop | *This is the name of the pipeline* |
   | How do you want to setup your pipeline? | Inline | *Harness (rather than Git) will be the source of truth for the pipeline* |

**4.** From Pipeline Studio, click **Add Stage** and select **Build** as the Stage Type

**5.** Enter the following values and click on **Set Up Stage**

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Stage Name | Build | |
   | Clone Codebase | Enabled | *The codebase will be cloned automatically* |
   | Repository Name | harnessrepo | |

**6.** There are **three** main tabs that need configuration:

   ### Overview

   Under **Cache Intelligence**, expand **Advanced (Optional)** and configure:

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Paths | /harness/frontend-app/.m2/repository | *Path for the Maven repository cache* |
   | Key | maven-cache | *A unique key to identify this cache entry* |

   ### Infrastructure

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Infrastructure | Cloud | *Harness Cloud provides managed build infrastructure on demand* |

   ### Execution

   - Select **Add Step**, then **Add Step** again, then select **Test Intelligence** from the Step Library and configure with the following

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | Run Tests With Intelligence | |
   | Command | cd frontend-app && mvn test | *Our monorepo requires navigating to the application subfolder* |

   Under **Optional Configuration**:

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Container Registry | dockerhub | *Click the **Project** tab in the connector popup to find it, then select **Apply Selected*** |
   | Image | maven:3.9-eclipse-temurin-17 | *Provides Maven + JDK 17 for the build* |
   | Intelligence Mode | Enabled | *Only runs tests affected by your code changes* |

   - After completing configuration select **Apply Changes**

   #### Compile & Push

   - Select **Add Step**, then **Use template** — we'll use a pre-created template to compile the application and avoid reinventing the wheel

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Template Name | Maven Package | *A reusable template for building the application with Maven* |

   - Select the template and press **Use Template,** then provide a name for that template

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name  | Compile | *Name of the template in the pipeline* |

   - Select **Add Step**, then **Add Step** again, then select **Build and Push an image to Docker Registry** from the Step Library and configure with the following

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name  | Push to Dockerhub | |
   | Registry Type | Third-Party Artifact Registry | |
   | Docker Connector | dockerhub | |
   | Docker Repository | nikpap/harness-workshop | |
   | Tags | <+variable.username>-<+pipeline.sequenceId> | *Click on the pin icon, select **Expression**, and paste the value* |
   | **Optional Configuration** | | |
   | Dockerfile | /harness/frontend-app/Dockerfile | *Points Harness to the frontend Dockerfile* |
   | Context | /harness/frontend-app | *The build context for the Dockerfile instructions* |

   - Click **Apply Changes** to close the config dialog

**7.** Click **Save** and then click **Run** to execute the pipeline with the following inputs

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Branch Name | spring | *This is prepopulated* |

---

# Lab 2 - DevSecOps

## Summary
Security shouldn't be an afterthought, it should be baked right into your pipeline. In this lab, you'll integrate **Sonarqube**, **Veracode**, and **Kodem** security scans into your CI pipeline using reusable templates provided by your security team. No security expertise required on your end, just plug in the templates and let the scanners do the heavy lifting.

## Objectives

- Integrate security scanning into your CI pipeline using reusable templates
- Understand how governance policies enforce security standards in the path to production
- Review deduplicated, normalized vulnerability findings across multiple scanners

## Why It Matters
Shifting security left means catching vulnerabilities before they ever reach production, not scrambling to fix them after the fact. This lab shows how reusable templates make DevSecOps practices easy to adopt, and how governance policies ensure no one skips the security checks. Secure by default, not by accident.

## Steps

![](https://lh7-us.googleusercontent.com/docsz/AD_4nXcLr5TGcKRWOjVgB_sCAHHEeLPyd6EBdnkt2-mq_imTkZbQMEwJD03Q1wZyhWqHxoCNIIYWJWlRbnZrvZn2pPYIwTzXlOGdhMDEgn-J2JnK7lVastmfpdwTqDHXjpP0DK3TgU1gM-Ec_0iZLicWV7KpgW2FdXUCcAtraDGaEz8hI3dpWGLXkg?key=cRG2cvp_PHVW0KG2Gq6Y_A)

**1.** In the existing pipeline, within the Build stage **before** the **Push to Dockerhub** step, click the **+** icon to add a new step

**2.** Select **Use template**

![](https://lh7-us.googleusercontent.com/docsz/AD_4nXeC5rTVxlk7DeZeU_cINwcKo6Nf2wVW9brQ9MiCEfppJwmU-uH3QcNZ53qTxhur57KeySksoDBg9EqjhgKOgAEDKon6iNz9cFxozBe9VZssV-t77VNo6t1zPUvm6e2NOZJDKncxd9c2GM4HE-h-L4cIOl4u6Uqx_azoKchMdg?key=cRG2cvp_PHVW0KG2Gq6Y_A)

**3.** Select **Sonarqube** and name the step **Sonarqube**

**4.** Repeat the process for **Veracode** and **Kodem** — but this time, add them **in parallel** instead of in series. Hover under the **Sonarqube** step and click the **+** icon to add each one as a parallel step. Name them **Veracode** and **Kodem** respectively.

![Build stage with security steps](images/lab2-build-steps.png "Build stage with security steps")

**5.** Click **Save** and then click **Run** to execute the pipeline with the following inputs

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Branch Name | spring | |

**6.** After the **Build and Push** stage is complete, go to the **Security Tests** tab to see the deduplicated, normalized, and prioritized list of vulnerabilities discovered across your scanners.

---

# Lab 3 - Continuous Deployment - Frontend

## Summary: 
Our application compiled successfully and the artifact is in Dockerhub. Time to deploy it. Extend the pipeline to ship the frontend to a Kubernetes cluster using a rolling deployment. The manifests are ready, no manual kubectl commands, no deployment scripts to maintain, just point Harness at your manifests and let it handle the rest.

## Objectives

- Extend CI pipelines with Continuous Deployment stages
- Define Kubernetes services with manifests and artifact sources
- Use Harness expressions for dynamic artifact tagging
- Implement rolling deployment strategies

## Why It Matters
This lab demonstrates how teams can quickly and easily deploy software without custom scripting, leveraging native rolling deployment capabilities. The lab goes under the hood to show what teams deploy (the Harness Service) and where they deploy it (the Harness Environment) are decoupled from the deployment logic defined in the pipeline. This decoupled architecture unlocks pipeline standardization at scale. 

## Steps

**1.** In the Pipeline Studio, add a Deployment stage by clicking **Add Stage** and select **Deploy** as the Stage Type.

**2.** Enter the following values and click on **Set Up Stage**

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Stage Name | Frontend Deployment | |
   | Deployment Type | Kubernetes | |

![Click on the plus icon to add a new stage](images/lab2-deploy-stage.gif "Add Stage")

**3.** Configure the **frontend** Stage with the following

   ### Service

   - Click **+Add Service** and configure as follows

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | frontend | _This is preselected for you based on the Stage type_|
   | Deployment Type | Kubernetes | |
   | **Add Manifest** | | |
   | Manifest Type | K8s Manifest | |
   | K8s Manifest Store | Code | |
   | Manifest Identifier | templates | |
   | Repository | harnessrepo | |
   | Branch | spring | |
   | File/Folder Path | harness-deploy/frontend/ocp/manifests | |
   | Values.yaml | harness-deploy/frontend/ocp/values.yaml | _Click **Submit** to go back and continue configuration of the artifact source_ |
   | **Add Artifact Source** | | |
   | Artifact Repository Type | Docker Registry | |
   | Docker Registry Connector | dockerhub | |
   | Artifact Source Identifier | frontend | |
   | Image Path | nikpap/harness-workshop | |
   | Tag | <+variable.username>-<+pipeline.sequenceId> | _Click on the purple "**Sigma**" icon to the right of the text box. A "Learn More" pop up will appear and block your view, click the "**x**" to exit it. Then select **Expression** from the dropdown and paste the value._ |

   - Click **Save** to close the service window and then click **Continue** to go to the Environment tab

![Create the frontend service](images/lab2-frontend-svc-har.gif "Create Service")

   ### Environment

   The target infrastructure has been pre-created for us. The application will be deployed to a Kubernetes cluster on the given namespace  

   - Click **- Select -** on the **"Specify Environment"** input box

   - Select **prod** environment and click **"Apply Selected"**

   - Click **- Select -** on the **"Specify Infrastructure"** input box

   -  From the dropdown select **ROSA** and click **"Apply Selected"**

   - Click **Continue** 

   ### Execution Strategies

   - Select **Rolling** and click on **Use Strategy**, the frontend is a static application so no need to do canary.

   - **Save** the pipeline.

![Add the environment](images/lab2-frontend-env.gif "Add Environment")

---
# Lab 4 - Continuous Deployment - Backend

## Summary
Frontend is done. Now for the backend, where things can actually break in expensive ways. Let's use a canary deployment strategy with a manual approval before a broad rollout in order to minimize the blast radius. Deploy to a small slice of traffic, verify the canary is healthy, then promote to everyone. Progressive delivery made easy. 

## Objectives
- Extend the pipeline with multiple deployment stages for different services
- Implement advanced deployment strategies to reduce blast radius of a failed release
- Add manual approval gates and keep the human in the loop for controlled production releases

## Why It Matters
This lab validates Harness’s ability to safely deploy changes to production using advanced deployment strategies. Participants experience how risk is reduced through progressive delivery and manual validation, without complex scripting.
## Steps
**1.** In the existing pipeline, add a Deployment stage by clicking **Add Stage** and select **Deploy** as the Stage Type

**2.** Enter the following values and click on **Set Up Stage**

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Stage Name | Backend Deploy | |
   | Deployment Type | Kubernetes | |

**3.** Configure the **backend** Stage with the following

   ### Service

   - Click **- Select -**  on the **"Select Service"** input box and select **backend** (this was preconfigured for you)

   - Click **Apply Selected** and then click **Continue** to go to the **"Environment"** tab

   ### Environment
   - Click **- Select -** on the **"Specify Environment"** input box

   - Select **prod** environment and click **"Apply Selected"**

   - Click **- Select -** on the **"Specify Infrastructure"** input box

   -  From the dropdown select **ROSA** and click **"Apply Selected"**

   - Click **Continue** 

   ### Execution

   - Select **Canary** and click on **Use Strategy**

   - **After** the canary deployment and **before** the canary delete step add **Harness Approval** step according to the table below

     | Input | Value | Notes |
     | ----- | ----- | ----- |
     | Name  | Approval | |
     | User Groups | All Project Users | Select project to see the **"All Project Users"** option |

![Canary Approval](images/harness-approval.png "Approve the Canary Deployment")

   - Click **Apply Changes** at the top right.

**4.** Click **Save** and then click **Run** to execute the pipeline with the following inputs.

> **Bonus**: save your inputs as an Input Set before executing

   | Section | Input | Value | Notes |
   | ------- | ----- | ----- | ----- |
   | CI Codebase | Branch Name | spring | _Leave as is_ |
   | Stage: Frontend Deploy | Service > Primary Artifact | frontend | _Leave as is_ |
   | Stage: Backend Deploy | Service > Primary Artifact | backend | _Leave as is_ |
   | Stage: Backend Deploy | Service > Tag | backend-v1 | |

![Canary Deployment](images/lab3-canary.gif "Canary Deployment")

**5.** While the canary deployment is ongoing and waiting for **approval**, navigate to your deployed application to verify the canary is live.

   - Log in to the ROSA cluster at https://console-openshift-console.apps.rosa.u7s2r6r8i3b3v5k.4qrx.p3.openshiftapps.com/

   - Select **Log in with "powerpay"**

   - Enter your credentials: username is your **project ID**, password is the same one you used to log in to Harness _(refer to the user details spreadsheet provided by your instructor)_

   - Click **Project** from the left-hand sidebar

   - From the **Inventory** box, click **2 Routes**

   - Click the URL in the **Location** column for the **frontend-prod** route

   - See if you can spot Captain Canary _use the **Check Release** button or refresh the page_

![ROSA Login](images/rosa-login.gif "ROSA Login")

![Canary Deployment](images/canary.png "I see the canary!")

**6.** Approve the canary deployment for the pipeline to complete and go back to your app. You should see Captain Canary has left as his work here is done.

---
# Lab 6 - Policy, Governance & Change Management

## Summary
You've built a pipeline that builds, tests, and deploys your frontend and backend services. Now the compliance team wants a word. In regulated environments, you can't just ship code to production without following change compliance policies and maintaining an audit trail for traceability. In this lab, we'll enforce governance with Policy-as-Code, ensuring every pipeline has an approval gate, and integrate with ServiceNow for automated change management. Compliance as code, not compliance as bottleneck.

## Objectives

- Enforce governance guardrails using Policy as Code (OPA)
- Integrate ServiceNow for automated change request creation and approval
- Understand how policies prevent non-compliant pipelines from being executed, or even saved.

## Why It Matters
This lab proves that governance does not have to be manual, inconsistent, or slow. Participants validate how organizational policies are enforced automatically across all pipelines, eliminating human bottlenecks while preserving auditability.

## Steps

### Policy as Code

**1.** At the bottom of the Unified View left navigation bar hover over **Project Settings** and select **Policies** from the expanded menu.

**2.** At the top right click on **Policies** and select **Approval Required Policy** and review the policy.

**3.** Click on the **Select Input** button on the right and select these values from each dropdown:

   | Input | Value |
   | ----- | ----- |
   | Entity Type | Pipeline |
   | Organization | \<your-org\> |
   | Project | \<your-project\> |
   | Action | On Save |

**4.** Select your most recent pipeline save and click **Apply**.

**5.** Now click on the green **Test** button on the right. What do you think will happen?

> **Note:** Since the policy checks that we have an approval before any deployment stage, it's expected that it failed. Failure is success! The policy is working as designed.

![Policy as Code](images/lab5-opa.gif "Policy as Code")

**6.** Let's now enforce it. Click on **Policy Sets** from the top right.

**7.** Find the **Approval Required Policy Set** and click on the **Enforced** toggle to turn it on

### Governance in Action

**1.** Head back over to our pipeline by selecting **Pipelines** from the Unified View left navigation bar.

**2.** Let's make a small edit to our pipeline so we can save it. Click on the pencil icon next to the pipeline name.

**3.** Now click on the pencil icon next to the **Tags** section and add a tag. You can get creative here :)

**4.** Click **Continue** then **Save** your pipeline.

> **Note:** As we expected, we are not allowed to save our pipeline until we've added an Approval. Let's fix it!

![Policy Violation](images/lab5-policy-violation.gif "Policy Violation")

### Approvals via ServiceNow Change Requests

**1.** Hover before the **frontend** stage and click on the **+** icon that appears to add a new stage.

**2.** Click **Use Template**

**3.** Select the **SNOW Approval** template and click on **Use Template** in the lower right corner.

**4.** Name it `ServiceNow Approval` and click **Set Up Stage**.

> **Note:** Make sure you name it `ServiceNow Approval` as we will add steps later that reference this stage.

**5.** This template has been preconfigured for us, so there are no inputs necessary

**6.** Click on the **Overview** toggle to see the steps in this template. 

**7.** Click on the **Create Ticket** or **Approval** steps to see how they are configured and notice how you, as a template user, cannot change the configuration for this enterprise-approved template, only the template administrator can make changes. Click the **X** or **Discard** once you're done reviewing.

> **Note:** Notice the use of Harness Expressions to dynamically populate our tickets and approvals.

**8.** Save the pipeline. No violations this time, hooray for compliance!

![ServiceNow Approval](images/lab5-add-approval.gif "ServiceNow Approval")

> **Bonus:** Add a step to close the ServiceNow ticket after the last step of the **backend** stage, indicating that we've successfully deployed to production. *Hint: there's a template already created.*

---

# Lab 7 - Continuous Verification

## Summary
Canary deployments are great, but how do you know the canary is actually healthy? Continuous verification integrates with your observability tools and uses ML to compare metrics and logs against the baseline in real-time. No manual dashboard watching required. We'll also add chaos experiments to stress-test the deployment. If the canary survives intentional chaos, it's ready for production.

## Objectives
- Configure continuous verification to compare canary metrics against baseline using ML
- Add chaos experiments to stress-test deployments during the canary phase
- Automate go/no-go decisions based on real-time observability data

## Why It Matters
This lab validates how Harness detects deployment issues based on real system behavior, not just pipeline success. Participants experience how deployments are continuously verified using telemetry + AI/ML, enabling faster detection and rollback of bad releases before they impact users, eliminating the need for manual monitoring, and reducing mean time to resolution.

## Steps
**1.** Click on the **backend** deployment stage and hover over the **Approval** step. Delete it by clicking the **x**. We no longer need a manual approval since we will add automated deployment validation next.

**2.** Between the **Canary Deployment** and **Canary Delete** steps, click the **+** icon to add a new step

**3.** Add a **Verify** step with the following configuration

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | Verify | |
   | Continuous Verification Type | Canary | |
   | Sensitivity | High | _Defines how sensitive the ML algorithms are to deviation from the baseline_ |
   | Duration | 5mins | |

**4.** Within the Verify step configuration panel, select the **Advanced** tab and expand the **Failure Strategy** section. In the **Perform Action** configuration, change the behavior to **Rollback Stage**.

![Continuous Verification](images/lab6-cv.gif "Continuous Verification")

**5.** Under the Verify step, click the **+** icon to add a new step **in parallel**

   ![Add Parallel Step](https://github.com/user-attachments/assets/368ba808-d303-43f8-8824-5d2e09367b01)

**6.** Add a **Chaos** step with the following configuration

   | Input | Value | Notes |
   | ----- | ----- | ----- |
   | Name | Chaos | |
   | Select Chaos Experiment | <project_name>-pod-memory | _Select the existing experiment from the list_ |
   | Expected Resilience Score | 50 | _Should already be populated for you_ |

**7.** Click on Apply Changes

**8.** Click **Save**

![Chaos](images/lab6-chaos.gif "Chaos")

---

# Lab 8 - Release Validation & Automatic Rollback

## Summary
This is where it all comes together. Watch the entire delivery pipeline flow from commit to production: multi-service deployments, automated change management with ServiceNow approvals, canary deployments validated by ML-powered verification, and chaos experiments checking the resiliency of your release. If something breaks, the pipeline rolls back automatically. No war rooms, only pizza parties.

## Objectives
- Execute the full golden path pipeline end-to-end
- Observe canary vs baseline traffic distribution in real-time
- Approve ServiceNow change requests to progress deployments
- Validate automated rollback if verification fails

## Why It Matters
This lab demonstrates the full power of a modern CD platform by combining multiple safety mechanisms into a single, automated pipeline. You'll see how ServiceNow approvals ensure proper change control, how AI/ML-powered continuous verification provides objective health signals, and how chaos engineering validates real-world resilience - all working together to deliver software with confidence while maintaining system stability.

## Steps
**1.** First, we need to deploy a new version of our backend to the canary environment so we can demonstrate how to rollback a failed release. Click the **Run** button in the upper right corner to execute the pipeline but this time, select the backend-v2 in the dropdown box that pops up.

   | Section | Input | Value | Notes |
   | ------- | ----- | ----- | ----- |
   | CI Codebase | Branch Name | spring | _Leave as is_ |
   | Stage: Frontend Deploy | Service > Primary Artifact | frontend | _Leave as is_ |
   | Stage: Backend Deploy | Service > Primary Artifact | backend | _Leave as is_ |
   | Stage: Backend Deploy | Service > Tag | backend-v2 | _Update from v1 to v2_ |

![Select backend-v2](images/lab7-pick-backend-v2.png "Select backend-v2")

**2.** The pipeline will eventually pause on the ServiceNow Approval stage. At this point, the orchestration pipeline automatically created the SNOW change record on your behalf (the developer) and updated the ticket with the details needed for a release. No manual change records to maintain by the developer, everything is automated. Next, let's simulate a release manager signing off on the implementation.

- Click on the **ServiceNow Approval** stage, click on the **Approval** step, and click on the change record hyperlink in the step details on the right to open the change record in a new tab.

- Next, login to the SNOW sandbox instance with the name **`workshopuser`** and the same password you used to log in to the lab. Click the **`Implement`** button in the upper right corner. While you're there, observe the metadata provided by the pipeline. Click back to the Harness tab in your browser and observe the pipeline progressing once the change record was approved.

![ServiceNow Approval](images/lab7-snow-approval.gif "ServiceNow Approval")

**3.** As the pipeline progresses to the backend deployment, navigate back to your app and see if you can spot the canary (use the check release button or refresh the page).

- Validate that we've deployed the new version in the canary by checking the version is **backend-v2** and the Last Execution matches the **build Id** of your pipeline

![Canary Verify](images/lab7-canary-verify-v2.gif "Canary Verify")

------

**4.** Next we're going to generate some traffic to the canary to test the automated release validation. Click the **Start** button in the Distribution Test panel.

- Observe the traffic distribution. You should see traffic routing to a subset of the infrastructure.

![](https://lh7-us.googleusercontent.com/docsz/AD_4nXdbAmEJ5zQPsKlw_nEknWvYo97pm5eWCXr6vU8-GgIL0ulAOSH9N07PoEcVSknARVQo7Tgj1s31VHqR1I3hu2dMIO1rIX5HHcmTPXoQPoyo8CPv13OhnJN5WVcZqSwUXzdDHmm3PxUnhtpGVl0PAMJ_1wnuodvUbVPBOdnGKQ?key=cRG2cvp_PHVW0KG2Gq6Y_A)
![](https://lh7-us.googleusercontent.com/docsz/AD_4nXf-5oWX9OfvdmEb9MBm2_h2KKAa_QwmiJoM0fiKrTuxAr6GR4wxeulSlk48gyBK3dykrtIslDSkxpiGytrxH0JaxaQ4ZgTYxbmc8OenAH3nhGCvvOAxkWVjVBp1TRg_qQQi9z8OrNPK4udPtNL1LIyym6Ch5IMzrulFOcXhOQ?key=cRG2cvp_PHVW0KG2Gq6Y_A)

**5.** Switch back to the Harness tab in your browser to observe the pipeline behavior. The Chaos experiment and Continuous Verification steps will run for approximately 5 minutes. Wait for them to complete.

**6.** Once the Verify step completes, select it and notice that **1 out of 1 metric is in violation**. Click **View Details ->** to inspect the failure.

**7.** Harness ingests telemetry from Prometheus to determine the health of the release. You'll see that the **Pod Memory** metric is flagged as anomalous. Expand it to examine its behavior:

- The **solid red line** represents the canary's memory usage, which is growing in an unbounded way
- The **dotted blue line** represents control data from the previous stable release, showing no memory increase and serving as our baseline

> **Note:** This pattern points to a memory leak in **backend-v2** that would have been difficult to detect without this data. Traditionally, if a service starts up cleanly, we consider it a successful release. But issues like memory leaks only manifest under load over time, making them notoriously hard to diagnose. This is precisely why continuous verification is so crucial, it catches problems that traditional health checks miss.

**8.** Click the **Console View** toggle in the top right to return to the pipeline execution view.

**9.** Notice that because the Verify step failed, Harness automatically initiated a rollback of the canary to the previous stable version. This automated rollback capability eliminates the need for manual intervention during incidents, reduces mean time to recovery and ensures your users are protected from degraded experiences while your team investigates the root cause.

**10.** Finally, navigate back to the web application and verify that we've rolled back to **backend-v1**. Use the **Check Release** button to confirm the canary no longer appears.

![CV Failure & Rollback](images/lab7-cv-fail.gif "CV Failure & Rollback")

---

# Lab 9 - Automated Security Standards Enforcement

## Summary
Honor system enforcement of security scans is great. Automated enforcement of security scans and blocking bad deployments is better. Using policy-as-code OPA policies, ensure all deployments are scanned for vulnerabilities and automatically turn vulnerability findings into hard stops to ensure critical CVEs never reach production.

## Objectives
- Enforce security standards using centrally-managed OPA policies
- Integrate policy enforcement into deployment pipelines
- Block deployments with critical vulnerabilities before production

## Why It Matters
This lab demonstrates how to enforce security standards automatically across your organization using policy-as-code. By implementing centrally-managed OPA policies, you can ensure that all deployments meet your security requirements before they reach production, eliminating the risk of human error or bypassing of security controls.

## Steps

### Enable Automated Policy Enforcement

**1.** From the Unified View left navigation bar, scroll down and hover over to **Project Settings**. Select **Policies** from the expanded menu.

**2.** Select the **Policy Sets** tab from the top right.

**3.** Toggle the **Enforced** on for both **Criticals Not Allowed** and **Security Scans Required Policy Set**.
> **Note:** The underlying policies were pre-built in your project. _Criticals Not Allowed_ enforces the policy that blocks deployments with critical vulnerabilities. _Security Scans Required Policy Set_ ensures that all deployments have been scanned for security issues.

**4.** Navigate back to your pipeline by clicking **Pipelines** in the Unified View of the left navigation bar.

**5.** Like before, try making a simple change to your pipeline, like adding a tag to the pipeline name and click **Save** in the upper right to see how the policy enforcement behaves.

![STO OPA Failure](images/lab8-sto-required-error.gif "STO OPA Policy")

**6.** Align to the enterprise security standards by adding a new stage before the ServiceNow Approval. Hover over the pipeline and click the **+** button to add a new stage. On the pop-up, select **Use Template**.

**7.** Select the Security Scans template and click the **Use Template** button in the lower right corner.

**8.** Name the stage **Scan** and click **Set Up Stage**.

> **Note:** Make sure you name it `Scan` as the name is referenced in downstream policies.

**9.** Click **Save** in the upper right corner to save the changes to your pipeline. You are now in compliance with the enterprise security standards so you are allowed to save your changes.

![STO Template](images/lab8-sto-template.gif "STO Template")

### Automatically Block Critical CVEs in the Pipeline

**1.** Select the **frontend** stage. Hover over the pipeline before the **Rollout Deployment** step, and click the **+** button to add a new step. Then select **Use Template**.

**2.** Select the **Check Critical CVEs** template then click the **Use Template** button in the lower right corner.

**3.** Name the policy step template **Block CVEs** and click **Apply Changes**.

**4.** Save the pipeline and click **Run**. In the run options, select the **backend-v2** version.

![OPA Template](images/lab8-opa-template.gif "OPA Template")

> **Note:** Expect the pipeline to fail at the policy evaluation step. The OWASP scan found critical vulnerabilities, and the policy we just created is doing its job blocking the release before it reaches production. If you'd like to get back in compliance so the pipeline can proceed, navigate to **Project Settings --> Policies --> OWASP CVEs** and change the Rego policy on the left from `critical > 0` to `critical > 6`.

---

# Lab 10 - Enhanced Change Management Automation

## Summary
Close the loop on failed releases. Configure rollback steps that automatically update ServiceNow when deployments fail.

## Objectives
- Configure rollback-specific steps in deployment stages
- Leverage step group templates for automation of complex workflows

## Why It Matters
This lab shows how to close the loop on failed deployments by automatically updating ServiceNow when releases fail, ensuring proper change management and accountability even when automated rollbacks occur.

## Steps
**1.** Navigate to the Edit mode of the Pipeline Studio.

**2.** Click into the Deploy **backend** stage and toggle the Rollback view in the right corner.

![Rollback view](images/lab9-rollback-view.png "Rollback view")

**3.** Click the '**+**' button at the end of the pipeline to add a new step and select **Use Template**.

**4.** Select the ServiceNow Close Failed template and give it a name - Close Failed Ticket.

**5.** Apply the changes and Save the pipeline. 

![Rollback studio](images/lab9-rollback-studio.gif "Rollback studio")
