# ManageIt Project Defence Prep

This guide is written for a no-slides defence where you demonstrate the app on an emulator or phone and answer questions about app design, Java, UI, database design, queries, and implementation choices.

## 1. One-Minute Project Summary

ManageIt is an Android app for managing student groups. A user can register, log in, request enrollment into a group, enter a role-aware dashboard, and use modules for tasks, events, announcements, membership management, budgeting, finance news, and optional ML-based task cost prediction.

The app is not a single large Activity. It is organized into layers:

- UI layer: Activities, Fragments, XML layouts, adapters.
- State and navigation layer: SessionManager, UiModeManager, RoleNavigator.
- Business layer: use case classes such as LoginUseCase, RegisterUseCase, CreateBudgetUseCase, PredictTaskCostUseCase, RequestAdminAccessUseCase.
- Data layer: repositories such as AuthRepository, GroupMembershipRepository, BudgetRepository, GroupTasksRepository.
- Network layer: Retrofit interfaces and API clients.
- Model layer: plain Java model classes mapped from API and DB payloads.
- Optional backend layer: Spring Boot bridge between Android and Azure ML.

Good short answer:

"ManageIt uses a layered Android architecture. Activities and Fragments handle presentation, ViewModel and use cases hold flow logic where needed, repositories hide data access, Retrofit maps Java method calls to StudEV and external APIs, and model classes represent rows returned from the database. Role-based navigation decides whether the user sees an admin or user dashboard."

## 2. Evaluation Strategy

### App Design and Implementation: 8 points

Show that the app is structured and usable:

- Start from SplashActivity, AuthActivity, MainActivity, then group dashboard screens.
- Explain that only SplashActivity is exported in AndroidManifest.xml; internal screens are not exported.
- Explain MVVM in authentication: LoginFragment/RegisterFragment observe AuthViewModel LiveData.
- Explain Repository pattern: UI does not directly call Retrofit; repositories handle API calls and errors.
- Explain AppContainer as lightweight dependency injection.
- Explain adapters for repeated list rows.
- Explain role-aware UI: admins get management actions, users get read/review access.

### Database Design and Implementation: 4 points

Show the schema and relations:

- Users stores accounts.
- StudentGroup stores groups.
- GroupMembership links users to groups and stores role_in_group and membership_status.
- Request stores pending or processed access requests.
- Task, Event, Announcement are group-scoped modules.
- Budget, BudgetCategory, TaskBudget, Expense model the budgeting system.
- MlTaskCostHistory and MlTaskCostTrainingView support ML training data.

Mention constraints:

- Primary keys uniquely identify rows.
- Foreign keys connect child records to parent records.
- Many-to-many between Users and StudentGroup is resolved through GroupMembership.
- Admin-only writes are enforced in SQL by joining GroupMembership and checking role/status.
- Budget SQL prevents invalid totals, negative amounts, and over-allocation.

### Features and Innovation: 4 points

Highest-value features to demonstrate:

- Role-based dashboards and membership approval flow.
- Tasks, events, announcements, and group member management.
- Budget dashboard with active budget, categories, task allocations, expenses, actual cost syncing, variance, and pie chart.
- Finance news via Marketaux.
- Optional ML prediction through Android -> Spring Boot -> Azure ML.
- Offline fallback module: BlackjackActivity.
- Dark/light mode.

### Live Presentation and Q&A: 4 points

Use a calm walkthrough. Do not click randomly. Narrate what each screen proves technically.

## 3. Recommended Live Walkthrough

1. Open the app.
   - Mention SplashActivity checks SessionManager.
   - If not logged in, it opens AuthActivity.

2. Register or log in.
   - Mention AuthInputValidator validates client-side fields.
   - PasswordUtils hashes passwords with SHA-256 before sending them.
   - AuthViewModel exposes Resource<User> states: EMPTY, LOADING, SUCCESS, ERROR.

3. Show group list in MainActivity.
   - StudentGroupRepository loads groups.
   - GroupMembershipRepository checks whether current user belongs to each group.
   - AdminAccessRequestRepository checks pending enrollment requests.
   - GroupListAdapter displays different UI states.

4. Request access to a group.
   - Explain Request table and status flow.
   - User cannot enter a group until membership exists or admin approves.

5. Enter a group as admin.
   - GroupDashboardActivity receives group id, group name, and role through Intent extras.
   - RoleNavigator selects AdminDashboardFragment or UserDashboardFragment.

6. Show admin dashboard.
   - Explain membership management and pending access requests.
   - Approving a request creates/updates membership and changes access.

7. Show tasks.
   - GroupTasksRepository reads tasks by group.
   - Admin creates tasks; status can be updated.
   - Task has title, description, status, due date, priority, estimated hours, team size, assignment fields.

8. Show events and announcements.
   - Events use REST endpoints first and legacy StudEV endpoints as fallback.
   - Announcements are encoded for URL path safety.
   - Admin can create/update/delete announcements.

9. Show budget dashboard.
   - BudgetRepository loads active budget, overview, categories, and task budgets.
   - BudgetDashboardData groups these four pieces for UI binding.
   - Admin actions unlock only after current admin membership is resolved.
   - Create budget -> add categories -> allocate tasks -> add expenses -> actual cost sync.
   - Explain remaining_to_allocate, remaining_to_spend, variance_from_allocated.

10. Show ML prediction.
   - Android creates PredictTaskCostRequest.
   - MlPredictionRepository calls backend /api/ml/predict-task-cost.
   - Spring Boot backend validates and converts fields, calls Azure ML, and returns predictedCostAmount.
   - Android displays predicted cost and model metadata.

11. Show dark/light toggle and about/offline if time.

## 4. Architecture Map

Important files:

- app/src/main/AndroidManifest.xml: app permissions and Activities.
- app/src/main/java/com/example/manageit/ManageItApplication.java: creates AppContainer and applies saved UI mode.
- app/src/main/java/com/example/manageit/di/AppContainer.java: central object wiring.
- app/src/main/java/com/example/manageit/network/ApiClient.java: singleton Retrofit client for StudEV.
- app/src/main/java/com/example/manageit/network/ApiService.java: API endpoint declarations.
- app/src/main/java/com/example/manageit/viewmodel/AuthViewModel.java: MVVM state for auth.
- app/src/main/java/com/example/manageit/repository/*.java: data access layer.
- app/src/main/java/com/example/manageit/models/*.java: data models mapped from JSON/DB.
- backend/src/main/java/com/example/manageit/backend/controller/MlPredictionController.java: backend endpoint.
- backend/src/main/java/com/example/manageit/backend/service/AzureMlPredictionService.java: Azure ML bridge.

Key flow:

UI event -> Activity/Fragment method -> use case or repository -> Retrofit Call<T> -> StudEV/API/backend -> Callback -> RepositoryCallback -> UI update.

Example login:

LoginFragment -> AuthViewModel.login -> AuthInputValidator -> LoginUseCase -> AuthRepository.login -> PasswordUtils.sha256 -> ApiService.login -> RepositoryCallback<User> -> LiveData<Resource<User>> -> UI observes result.

Example budget:

BudgetDashboardFragment -> BudgetRepository.getBudgetDashboardData -> GetActiveGroupBudget -> GetGroupBudgetOverview -> GetGroupBudgetCategories -> GetGroupTaskBudgets -> BudgetDashboardData -> bindOverview/bindCategories/bindTaskBudgets.

## 5. Java Concepts From The Slides, Mapped To Your App

### OOP, Classes, Objects

A class is a blueprint; an object is a runtime instance. Your app uses classes for each responsibility:

- User, Task, Budget are data objects.
- AuthRepository performs authentication data operations.
- MainActivity is a screen controller.
- BudgetDashboardFragment is a reusable UI part hosted inside an Activity.

Defence answer:

"I used classes to separate responsibilities. Models represent data, repositories perform data access, activities/fragments control UI, and managers hold app-level state."

### Encapsulation

Encapsulation means hiding implementation details and exposing controlled methods.

Examples:

- ApiClient hides Retrofit construction behind getApiService().
- SessionManager hides SharedPreferences keys behind createSession(), clearSession(), getCurrentUser().
- PasswordUtils has a private constructor and exposes static sha256().
- Model fields are private with getters/setters.

Good answer:

"The UI does not know how SharedPreferences or Retrofit are configured. It calls SessionManager or repositories, which keeps implementation details hidden and easier to change."

### Inheritance

Android is heavily inheritance-based:

- MainActivity extends AppCompatActivity.
- BudgetDashboardFragment extends Fragment.
- ManageItApplication extends Application.
- Backend service/controller use Spring annotations but still inherit from Object implicitly.

Good answer:

"I mostly use framework inheritance where Android requires it. Activities extend AppCompatActivity and Fragments extend Fragment. For my own architecture I prefer composition and interfaces over deep inheritance."

### Polymorphism and Dynamic Binding

Polymorphism means a variable of a supertype/interface can point to different concrete implementations.

Examples:

- RepositoryCallback<T> is an interface. Each API call passes an anonymous implementation with onSuccess/onError.
- AuthRepositoryContract can refer to AuthRepository.
- BudgetRepositoryContract can refer to BudgetRepository.
- List<Task> can be backed by an ArrayList returned by Gson/Retrofit.
- Callback<ResponseBody> from Retrofit is implemented anonymously many times.

Good answer:

"The repository code depends on callback and contract interfaces, not only concrete classes. At runtime, Java dispatches to the actual anonymous callback implementation."

### Interfaces

Interfaces define a contract without exposing implementation.

Examples:

- ApiService is a Retrofit interface. Retrofit generates the implementation at runtime.
- RepositoryCallback<T> defines onSuccess and onError.
- AuthRepositoryContract, BudgetRepositoryContract, MlPredictionRepositoryContract define repository behavior.
- Android listener interfaces are used for click handling and adapters.

Good answer:

"ApiService is a strong example of programming to an interface. I declare what endpoints exist, and Retrofit supplies the how."

### Anonymous Inner Classes and Callbacks

Your code uses anonymous inner classes for async Retrofit callbacks:

- new Callback<List<User>>() { onResponse(...), onFailure(...) }
- new RepositoryCallback<User>() { onSuccess(...), onError(...) }

Why:

- Network calls are asynchronous.
- UI thread should not block.
- The callback receives the result later.

Good answer:

"enqueue() executes asynchronously and calls onResponse or onFailure later. I convert Retrofit callbacks into RepositoryCallback so the rest of the app gets a simple success/error contract."

### Generics

Generics allow type-safe reusable classes and interfaces.

Examples:

- Resource<T> wraps Resource<User>, Resource<List<Task>>, etc.
- RepositoryCallback<T> works for User, Void, List<Task>, BudgetDashboardData.
- Call<List<User>>, Call<ResponseBody>.
- List<StudentGroup>, Map<String,String>.

Good answer:

"Resource<T> and RepositoryCallback<T> avoid duplicate success/error classes for every model type while keeping compile-time type safety."

### Collections

Collections are used throughout:

- List<T> for API result sets such as List<Task>, List<StudentGroup>.
- ArrayList in BudgetDashboardData and BudgetDashboardFragment.
- HashSet in filterAvailableTasks to avoid allocating the same task twice.
- HashMap in GroupEventsRepository to build JSON bodies.

Big O example:

- filterAvailableTasks first stores allocated task ids in a HashSet, then checks each task. Expected lookup is O(1), so the whole method is roughly O(n + m), better than nested loops O(n*m).

### Map and Hashing

HashMap stores key-value pairs.

Examples:

- GroupEventsRepository uses Map<String,String> body for REST request JSON.
- SharedPreferences internally behaves like key-value storage.

Good answer:

"For request bodies I used Map<String,String> because the payload is a small key-value structure. Gson/Retrofit can serialize it into JSON."

### Static

Static belongs to the class, not to an instance.

Examples:

- ApiClient.getInstance() is static and returns the singleton.
- Role.from() is a static factory/helper.
- PasswordUtils.sha256() is static because it does not need object state.
- Resource.success/error/loading/empty are static factory methods.
- Constants are static final.

Good answer:

"I use static methods for stateless utility behavior and factory-style creation. I avoid static for stateful business logic except the singleton ApiClient."

### Singleton

Singleton ensures one shared instance.

Example:

- ApiClient has private static ApiClient instance, private constructor, synchronized getInstance().
- It lazily creates Retrofit and ApiService.

Why:

- All repositories share base URL and HTTP configuration.
- Retrofit setup is expensive and should be centralized.

Caution:

- Singleton creates global state, so it should be used carefully. Here it is acceptable for one API client.

### Enum

Role is an enum with ADMIN and USER.

Why:

- Type safe compared to raw strings.
- Avoids invalid roles in Java code.
- Role.from() safely converts server string to enum and defaults to USER.

### Exceptions

Checked exceptions represent external recoverable problems. Unchecked exceptions usually represent programming or invalid state problems.

Examples:

- PasswordUtils catches NoSuchAlgorithmException and throws IllegalStateException because SHA-256 should exist on Android.
- BudgetRepository catches IOException when reading ResponseBody.
- AzureMlPredictionService throws IllegalArgumentException for invalid request fields and IllegalStateException for missing backend config.
- Retrofit network errors arrive through onFailure rather than thrown directly to UI.

Good answer:

"I handle expected network/API problems through callbacks and user-friendly messages. For impossible configuration problems, the backend throws exceptions that the exception handler can map to API errors."

### Clean Code and SRP

Single Responsibility Principle:

- AuthInputValidator only validates input.
- PasswordUtils only hashes passwords.
- ApiErrorMapper maps API failures to messages.
- BudgetRepository handles budget data access.
- BudgetDashboardFragment still has many UI responsibilities, but repository and model logic are separated from it.

Clean naming:

- getBudgetDashboardData, createTaskBudget, updateTaskBudgetActualCost, resolveMembership.

Refactoring examples already present:

- AppContainer centralizes construction.
- Use cases separate business actions from repositories.
- BudgetDashboardData groups related data instead of passing many lists separately.

### Design Patterns

MVVM/Observer:

- AuthViewModel exposes LiveData.
- Login/Register UI observes auth state.

Repository:

- Repositories hide where data comes from and how errors are handled.

Use Case:

- LoginUseCase, RegisterUseCase, CreateBudgetUseCase wrap business actions.

Singleton:

- ApiClient.

Factory/static factory:

- Resource.success/error/loading/empty.
- Role.from().

Adapter/ViewHolder:

- GroupListAdapter, GroupMemberAdapter, GroupTaskAdapter, etc.

Facade:

- BudgetRepository acts like a facade over multiple budget endpoints.

Strategy-like thinking:

- RoleNavigator chooses a dashboard strategy based on role.
- GroupEventsRepository tries REST first, then legacy fallback.

### Functional Programming and Lambdas

The project is Java 11 but uses mostly anonymous classes because Android callbacks are explicit and readable. Some lambdas are used for short UI listeners:

- button.setOnClickListener(v -> ...)
- listView.setOnItemClickListener((parent, view, position, id) -> ...)

Good answer:

"I used lambdas where the interface has one method and the behavior is short, like click listeners. For larger async callbacks I kept anonymous classes because onSuccess/onError or onResponse/onFailure are clearer."

### Concurrency and Threads

Android has a main UI thread. Long network work should not block it.

Your app uses Retrofit enqueue():

- enqueue() is asynchronous.
- onResponse/onFailure run later.
- UI is updated only after callbacks return.

Thread-safety note:

- Some callbacks check isFinishing(), isDestroyed(), or isAdded() before touching UI, preventing updates to destroyed screens.
- SharedPreferences apply() writes asynchronously.

Good answer:

"Network calls are asynchronous through Retrofit enqueue(), so the UI remains responsive. I guard UI updates with lifecycle checks like isAdded() in fragments."

## 6. Android Concepts To Know

### Activity vs Fragment

- Activity is a top-level screen/window.
- Fragment is a reusable piece of UI hosted inside an Activity.
- GroupDashboardActivity hosts either AdminDashboardFragment or UserDashboardFragment.
- GroupBudgetActivity hosts BudgetDashboardFragment.

### Intent

Used to navigate between screens and pass extras.

Example:

MainActivity.openDashboard passes EXTRA_GROUP_ID, EXTRA_GROUP_NAME, EXTRA_GROUP_ROLE to GroupDashboardActivity.

### Manifest

AndroidManifest.xml declares permissions and Activities.

- INTERNET and ACCESS_NETWORK_STATE are needed for APIs.
- SplashActivity is exported=true because it is the launcher.
- Other Activities are exported=false because they are internal.
- ManageItApplication is registered as the application class.

### SharedPreferences

SessionManager stores login session locally:

- is_logged_in
- user id
- first/last name
- dob
- email

It does not store the password.

### Retrofit and Gson

Retrofit maps Java interface methods to HTTP requests. Gson maps JSON fields to Java models using @SerializedName.

Example:

@SerializedName("group_id") maps database/API field group_id to Java field groupId.

### BuildConfig

Gradle injects values:

- MARKETAUX_API_BASE_URL
- BACKEND_API_BASE_URL
- MARKETAUX_API_TOKEN

This keeps local tokens and backend URLs out of source code.

## 7. Database Design

### Main Tables

Users:

- user_id primary key.
- first_name, last_name, dob, email, password_hash, created_at.
- Represents app accounts.

StudentGroup:

- group_id primary key.
- group_name, group_description, created_at, is_active.
- Represents groups visible on MainActivity.

GroupMembership:

- membership_id primary key.
- user_id foreign key to Users.
- group_id foreign key to StudentGroup.
- role_in_group: admin or user.
- membership_status: active etc.
- Resolves many-to-many relationship between users and groups.

Request:

- request_id primary key.
- user_id and group_id.
- request_type/description/status/created_at.
- Used for enrollment/admin access flow.

Task:

- task_id primary key.
- group_id foreign key.
- title, description, status, due_date, priority, estimated_hours, team_size.
- assigned_to_membership_id, assigned_by_membership_id.

Event:

- event_id primary key.
- group_id foreign key.
- event_name, event_description, event_date/event_datetime, created_by.

Announcement:

- announcement_id primary key.
- group_id foreign key.
- title, message, created_at, created_by_membership_id.

Budget:

- budget_id primary key.
- group_id foreign key.
- name, description, total_amount, currency_code, period_start, period_end, status.
- created_by_membership_id.

BudgetCategory:

- budget_category_id primary key.
- budget_id foreign key.
- category_name, allocated_amount, notes, display_order.

TaskBudget:

- task_budget_id primary key.
- budget_id, budget_category_id, task_id foreign keys.
- allocated_amount, predicted_cost_amount, actual_cost_amount.
- ml_prediction_status, ml_model_version, prediction_generated_at.

Expense:

- expense_id primary key.
- task_budget_id foreign key.
- expense_title, expense_amount, expense_date, vendor_name, notes.
- created_by_membership_id.

MlTaskCostHistory:

- historical training data with group_id, task_name, category_name, priority, estimated_hours, team_size, allocated_amount, actual_cost, completion_status, task_completed_at.
- Foreign key to StudentGroup.
- CHECK constraints for priority, non-negative amounts, team_size >= 1, estimated_hours >= 0, completion_status.

MlTaskCostTrainingView:

- View that selects completed ML history rows and computes variance = actual_cost - allocated_amount.

### Relationships

- Users 1-to-many GroupMembership.
- StudentGroup 1-to-many GroupMembership.
- StudentGroup 1-to-many Task/Event/Announcement/Budget/Request.
- Budget 1-to-many BudgetCategory.
- BudgetCategory 1-to-many TaskBudget.
- Task 1-to-0/1 or 1-to-many TaskBudget depending policy.
- TaskBudget 1-to-many Expense.
- GroupMembership is referenced by created_by_membership_id fields.

### Normalization

The schema is mostly normalized:

- User data is stored once in Users, not repeated in every group.
- GroupMembership avoids repeating group fields in user rows.
- BudgetCategory avoids repeating category details inside every expense.
- Expense rows reference TaskBudget instead of copying task and budget details.

Defence answer:

"The design avoids many-to-many duplication by using junction tables. Users and groups are connected through GroupMembership, which also stores role and status because those attributes belong to the relationship, not only to the user or group."

## 8. Important SQL/DBMS Concepts

### Primary Key

A primary key uniquely identifies a row. Example: user_id, group_id, budget_id.

### Foreign Key

A foreign key enforces valid references between tables. Example: MlTaskCostHistory.group_id references StudentGroup(group_id). If a group is deleted, ON DELETE CASCADE removes dependent ML history.

### Join

JOIN combines rows across related tables.

Example from budget allocation update:

- BudgetCategory joins Budget to know total budget.
- Budget joins GroupMembership to check the current member belongs to that group.
- The query allows update only if membership is active admin.

### Aggregation

SUM and GROUP BY compute totals.

Example:

- SUM(allocated_amount) grouped by budget_id computes total allocated per budget.
- SUM(allocated_amount) grouped by budget_category_id computes task allocations per category.

### COALESCE

COALESCE replaces NULL with a default value.

Example:

COALESCE(task_sums.task_allocated, 0) treats no task allocations as 0.

### View

A view is a saved query.

MlTaskCostTrainingView exposes only completed historical rows and computes variance. It makes model training easier because scripts can query one consistent shape.

### Constraints

Constraints protect the database even if the app has a bug:

- CHECK priority IN ('LOW','MEDIUM','HIGH').
- CHECK allocated_amount >= 0.
- CHECK team_size >= 1.
- Foreign keys prevent orphan rows.

### Transactions

StudEV services likely execute each service query atomically. If asked about stronger consistency:

"For multi-step operations, a transaction would be ideal so either all related changes commit or none do. In this project, many important checks are placed directly in single SQL update/insert statements, which reduces race conditions."

### SQL Injection and Encoding

StudEV endpoints use path parameters. The app encodes path values with Uri.encode or URLEncoder for fields such as announcements, events, descriptions, and budget names.

However, safest backend practice is parameterized SQL. In defence:

"Because the StudEV service binds placeholders like :userid and :groupid, the query should be parameterized server-side. On Android I also encode path values to avoid breaking URLs."

## 9. Query Examples You Should Be Able To Explain

### Unenroll From Group

The documented SQL deletes both:

- GroupMembership row.
- Related Request row with description requested_user_enrollment.

It uses DELETE gm, r FROM GroupMembership gm LEFT JOIN Request r ... WHERE gm.membership_id = (:membershipid).

Why LEFT JOIN:

- Delete membership even if there is no matching request row.

### Update Budget Category Allocation

The documented SQL:

- Joins BudgetCategory -> Budget.
- Joins GroupMembership to verify admin permission.
- Computes current total category allocations.
- Computes current task allocations under that category.
- Updates only when:
  - membership belongs to same group,
  - membership role is admin,
  - membership_status is active,
  - new allocation is non-negative,
  - total category allocation stays <= total budget,
  - category allocation remains >= amount already allocated to tasks.

Good answer:

"This is defensive database design. Even if the UI accidentally enables a wrong button, the SQL rejects over-allocation or non-admin writes."

### ML Training View

MlTaskCostTrainingView selects completed rows from MlTaskCostHistory and adds variance.

Why:

- ML training should use completed tasks where actual cost is known.
- It separates historical/synthetic training data from live operational TaskBudget data.

## 10. API and Backend

### StudEV API

Base URL:

https://studev.groept.be/api/a25pt201/

ApiService maps endpoints such as:

- Login/{useremail}/{password}
- Registration/{firstname}/{lastname}/{dob}/{useremail}/{password}
- GetAllGroups
- GetUserGroupMembership/{userid}/{groupid}
- GetGroupTasks/{groupid}
- GetGroupBudgetOverview/{groupid}

StudEV caveat:

Some SQL services reuse placeholders, so Android repeats values in the URL. Example:

UpdateBudgetCategoryAllocation sends allocatedamount multiple times because the SQL placeholder appears multiple times.

### Marketaux

MarketauxNewsRepository uses BuildConfig.MARKETAUX_API_TOKEN and creates a search query based on group name:

- robotics -> robotics/automation/engineering
- ai -> AI/ML/semiconductor
- debate -> economics/policy/politics
- default -> business/markets/finance/technology

### ML Backend

Android does not call Azure ML directly.

Flow:

Android BudgetDashboardFragment -> MlPredictionRepository -> MlBackendApiClient -> Spring Boot /api/ml/predict-task-cost -> AzureMlPredictionService -> Azure ML endpoint.

Why use backend:

- Keep Azure ML key off the Android client.
- Validate and normalize request fields.
- Avoid exposing cloud endpoint details.
- Return a clean response to Android.

## 11. Security and Robustness

Security choices:

- Password is hashed with SHA-256 before registration/login.
- Password is not stored in SharedPreferences.
- Only launcher Activity is exported.
- API keys are loaded through local.properties into BuildConfig rather than hard-coded.
- Azure ML API key stays on backend, not Android.

Limitations you can honestly mention:

- SHA-256 alone is better than plaintext but production-grade password storage should use salted adaptive hashing such as bcrypt, scrypt, or Argon2 on the server.
- GET endpoints with passwords in path are not ideal because URLs can be logged. This is a StudEV service constraint. A production API should use POST over HTTPS with request body and server-side hashing/salting.
- Some legacy endpoints use path parameters, so the app encodes values carefully.

Robustness choices:

- Repositories map technical failures to user-friendly messages.
- Network failures use onFailure.
- Many UI callbacks check lifecycle state before updating UI.
- GroupEventsRepository supports REST first and legacy fallback.
- BudgetRepository rejects "0 rows affected" as a failed write.

## 12. Likely Defence Questions And Strong Answers

Q: Why did you use repositories?

A: To keep API/data logic out of Activities and Fragments. The UI asks for operations such as getGroupTasks or createBudget, while repositories handle Retrofit calls, response parsing, and errors.

Q: Where is MVVM used?

A: Authentication. LoginFragment/RegisterFragment use AuthViewModel. The ViewModel exposes LiveData<Resource<User>> and validation LiveData, so UI observes state changes instead of owning login logic.

Q: What is Resource<T>?

A: A generic wrapper for UI state. It can represent LOADING, SUCCESS with data, ERROR with message, or EMPTY. Because it is generic, Resource<User> and Resource<List<Task>> can use the same class.

Q: What is the difference between compile-time and runtime type in your app?

A: An interface variable such as AuthRepositoryContract has compile-time type AuthRepositoryContract, but the actual runtime object is AuthRepository. Java dispatches to the concrete implementation at runtime.

Q: Why is ApiService an interface?

A: Retrofit uses the interface method annotations to generate the HTTP implementation. This also keeps endpoint declarations centralized and readable.

Q: Why use an enum for Role?

A: It restricts roles to ADMIN and USER in Java code, avoids invalid strings, and makes role comparisons safer.

Q: Explain asynchronous network calls.

A: Retrofit enqueue() starts the request asynchronously. It returns immediately, so the UI thread is not blocked. Later, onResponse or onFailure is called, and the repository maps that to onSuccess/onError.

Q: How do you avoid updating a destroyed screen?

A: Activities check isFinishing/isDestroyed, and fragments check isAdded before updating views from callbacks.

Q: What design patterns are in your app?

A: Repository, MVVM/Observer, Singleton ApiClient, Adapter for list rows, use case/interactor layer, static factory methods in Resource and Role, and facade-like BudgetRepository.

Q: Why is GroupMembership needed?

A: Users and groups have a many-to-many relationship. A user can belong to many groups, and a group has many users. GroupMembership is the junction table and stores attributes of that relationship: role, status, joined date.

Q: Why not store role in Users?

A: Role is group-specific. The same user could be admin in one group and standard user in another.

Q: What prevents users from changing budget data if they are not admins?

A: The UI hides admin controls for non-admins, but the stronger protection is in SQL: budget write queries join GroupMembership and require role_in_group = 'admin' and membership_status = 'active'.

Q: Explain the budget data model.

A: Budget is the group-level total. BudgetCategory splits the total by category. TaskBudget allocates part of a category to a task and tracks predicted and actual cost. Expense records individual spending under a task budget. Overview queries aggregate totals.

Q: What is variance?

A: In budgeting, variance is the difference between actual cost and allocated or predicted cost. Positive variance means over budget; negative means under budget.

Q: What is the purpose of MlTaskCostHistory?

A: It stores historical completed task cost examples separately from live operational data so ML training can happen without polluting active budgets.

Q: What is a view and why use MlTaskCostTrainingView?

A: A view is a saved query. MlTaskCostTrainingView filters to completed rows and adds derived variance so training scripts consume consistent data.

Q: What are primary and foreign keys in your schema?

A: Primary keys identify rows, such as user_id and budget_id. Foreign keys link tables, such as group_id in GroupMembership referencing StudentGroup and task_budget_id in Expense referencing TaskBudget.

Q: What is normalization in your database?

A: Data is split into tables by entity to avoid duplication. For example, user details are in Users, group details are in StudentGroup, and relationship details are in GroupMembership.

Q: Why does UpdateBudgetCategoryAllocation use subqueries?

A: It calculates current sums before allowing the update. One subquery calculates total allocated across categories, another calculates allocated task budget inside the category.

Q: Why do some API methods repeat the same path value?

A: StudEV binds placeholders by occurrence order in the SQL service. If the SQL uses the same placeholder multiple times, Android sends repeated path values to satisfy each occurrence.

Q: Why use a backend for ML instead of calling Azure from Android?

A: To protect the Azure API key and scoring URL, validate input server-side, and provide a stable app-facing API.

Q: What is the weakness of hashing in your app?

A: The app hashes the password before sending, which avoids sending raw text to StudEV, but production systems should do password hashing server-side with salt and adaptive algorithms. Also, credentials should not be in URL paths.

Q: How would you improve the app if you had more time?

A: Add stronger automated tests, migrate all legacy GET-style write endpoints to POST/PATCH with JSON bodies, add server-side auth tokens, use server-side salted password hashing, and move more large UI logic from BudgetDashboardFragment into ViewModel/use-case classes.

## 13. "Own The Weaknesses" Answers

If asked why BudgetDashboardFragment is large:

"It grew because the budget feature has many dialogs and UI states. The data access is already in BudgetRepository, but a future refactor would move more UI state and validation into a ViewModel to make the Fragment smaller."

If asked why Java not Kotlin:

"The course and project are Java-focused, and using Java made it easier to apply the lecture concepts directly: interfaces, generics, callbacks, collections, design patterns, and Android framework inheritance."

If asked why no Room/local DB:

"The project uses StudEV/MySQL services as the source of truth. A future improvement would be a Room cache for offline access and better local persistence."

If asked whether the app is fully secure:

"It has some security-conscious decisions, like not storing passwords locally and keeping Azure keys on the backend, but I would not claim production-grade security. Production auth should use HTTPS POST, server-side salted adaptive hashing, tokens, and authorization checks on every backend endpoint."

## 14. Quick Memory Sheet

Architecture:

UI -> ViewModel/use case -> Repository -> Retrofit -> API/DB -> callback -> UI.

Core design patterns:

Repository, MVVM/Observer, Singleton, Adapter, Factory, Facade.

Core Java concepts:

Encapsulation, interfaces, polymorphism, generics, collections, callbacks, enums, exceptions, static factories, asynchronous programming.

Core DB concepts:

Primary key, foreign key, junction table, join, aggregation, constraints, view, normalization, transactions.

Best demo order:

Login -> groups -> request/approval -> role dashboard -> tasks/events/announcements -> budget -> expenses -> ML prediction -> theme/offline.

Best closing line:

"The main goal was not only to add features, but to structure the app so each part has a clear responsibility: UI presents, repositories fetch and write data, models represent database records, and the database enforces important consistency rules."
