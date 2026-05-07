package com.example.manageit.network;

import com.example.manageit.models.Announcement;
import com.example.manageit.models.Budget;
import com.example.manageit.models.BudgetCategory;
import com.example.manageit.models.BudgetOverview;
import com.example.manageit.models.Event;
import com.example.manageit.models.Expense;
import com.example.manageit.models.GroupMember;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Request;
import com.example.manageit.models.StudentGroup;
import com.example.manageit.models.Task;
import com.example.manageit.models.TaskBudget;
import com.example.manageit.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("Login/{useremail}/{password}")
    Call<List<User>> login(
            @Path("useremail") String userEmail,
            @Path("password") String password
    );

    @GET("Registration/{firstname}/{lastname}/{dob}/{useremail}/{password}")
    Call<ResponseBody> register(
            @Path("firstname") String firstName,
            @Path("lastname") String lastName,
            @Path("dob") String dateOfBirth,
            @Path("useremail") String userEmail,
            @Path("password") String password
    );

    @GET("GetAllGroups")
    Call<List<StudentGroup>> getAllGroups();

    @GET("GetUserGroupMembership/{userid}/{groupid}")
    Call<List<GroupMembership>> getUserGroupMembership(
            @Path("userid") String userId,
            @Path("groupid") String groupId
    );

    @GET("CreateGroupMembership/{userid}/{groupid}/{roleingroup}")
    Call<ResponseBody> createGroupMembership(
            @Path("userid") String userId,
            @Path("groupid") String groupId,
            @Path("roleingroup") String roleInGroup
    );

    @GET("GetGroupTasks/{groupid}")
    Call<List<Task>> getGroupTasks(@Path("groupid") String groupId);

    @GET("CreateTask/{groupid}/{title}/{description}/{status}/{duedate}/{assignedto}/{assignedby}")
    Call<ResponseBody> createTask(
            @Path("groupid") String groupId,
            @Path("title") String title,
            @Path("description") String description,
            @Path("status") String status,
            @Path("duedate") String dueDate,
            @Path("assignedto") String assignedToMembershipId,
            @Path("assignedby") String assignedByMembershipId
    );

    @GET("UpdateTaskStatus/{taskid}/{status}")
    Call<ResponseBody> updateTaskStatus(
            @Path("taskid") String taskId,
            @Path("status") String status
    );

    @GET("GetGroupAnnouncements/{groupid}")
    Call<List<Announcement>> getGroupAnnouncements(@Path("groupid") String groupId);

    @GET("CreateAnnouncement/{groupid}/{title}/{message}/{createdbymembershipid}")
    Call<ResponseBody> createAnnouncement(
            @Path("groupid") String groupId,
            @Path(value = "title", encoded = true) String title,
            @Path(value = "message", encoded = true) String message,
            @Path("createdbymembershipid") String createdByMembershipId
    );

    @GET("UpdateAnnouncement/{title}/{message}/{announcementid}")
    Call<ResponseBody> updateAnnouncement(
            @Path(value = "title", encoded = true) String title,
            @Path(value = "message", encoded = true) String message,
            @Path("announcementid") String announcementId
    );

    @GET("DeleteAnnouncement/{announcementid}")
    Call<ResponseBody> deleteAnnouncement(@Path("announcementid") String announcementId);

    @GET("GetGroupMembers/{groupid}")
    Call<List<GroupMember>> getGroupMembers(@Path("groupid") String groupId);

    @GET("UpdateMemberRole/{roleingroup}/{membershipid}")
    Call<ResponseBody> updateMemberRole(
            @Path("membershipid") String membershipId,
            @Path("roleingroup") String roleInGroup
    );

    @GET("GetActiveGroupBudget/{groupid}")
    Call<List<Budget>> getActiveGroupBudget(@Path("groupid") String groupId);

    @GET("GetGroupBudgetOverview/{groupid}")
    Call<List<BudgetOverview>> getGroupBudgetOverview(@Path("groupid") String groupId);

    @GET("GetGroupBudgetCategories/{groupid}")
    Call<List<BudgetCategory>> getGroupBudgetCategories(@Path("groupid") String groupId);

    @GET("GetGroupTaskBudgets/{groupid}")
    Call<List<TaskBudget>> getGroupTaskBudgets(@Path("groupid") String groupId);

    @GET("CreateBudget/{groupid}/{name}/{description}/{totalamount}/{currencycode}/{periodstart}/{periodend}/{status}/{createdbymembershipid}/{createdbymembershipid_check}/{groupid_check}/{totalamount_check}/{periodend_check}/{periodstart_check}/{status_check}/{groupid_exists}")
    Call<ResponseBody> createBudget(
            @Path("groupid") String groupId,
            @Path(value = "name", encoded = true) String name,
            @Path(value = "description", encoded = true) String description,
            @Path("totalamount") String totalAmount,
            @Path(value = "currencycode", encoded = true) String currencyCode,
            @Path("periodstart") String periodStart,
            @Path("periodend") String periodEnd,
            @Path(value = "status", encoded = true) String status,
            @Path("createdbymembershipid") String createdByMembershipId,
            @Path("createdbymembershipid_check") String createdByMembershipIdCheck,
            @Path("groupid_check") String groupIdCheck,
            @Path("totalamount_check") String totalAmountCheck,
            @Path("periodend_check") String periodEndCheck,
            @Path("periodstart_check") String periodStartCheck,
            @Path("status_check") String statusCheck,
            @Path("groupid_exists") String groupIdExists
    );

    @GET("CreateBudgetCategory/{budgetid}/{categoryname}/{allocatedamount}/{notes}/{displayorder}/{createdbymembershipid}/{budgetid_check}/{allocatedamount_check_1}/{allocatedamount_check_2}")
    Call<ResponseBody> createBudgetCategory(
            @Path("budgetid") String budgetId,
            @Path(value = "categoryname", encoded = true) String categoryName,
            @Path("allocatedamount") String allocatedAmount,
            @Path(value = "notes", encoded = true) String notes,
            @Path("displayorder") String displayOrder,
            @Path("createdbymembershipid") String createdByMembershipId,
            @Path("budgetid_check") String budgetIdCheck,
            @Path("allocatedamount_check_1") String allocatedAmountCheck1,
            @Path("allocatedamount_check_2") String allocatedAmountCheck2
    );

    @GET("GetBudgetCategoryRemaining/{budgetcategoryid}")
    Call<List<BudgetCategory>> getBudgetCategoryRemaining(@Path("budgetcategoryid") String budgetCategoryId);

    @GET("UpdateBudgetCategoryAllocation/{createdbymembershipid}/{allocatedamount}/{budgetcategoryid}/{allocatedamount_check_1}/{allocatedamount_check_2}/{allocatedamount_check_3}")
    Call<ResponseBody> updateBudgetCategoryAllocation(
            @Path("createdbymembershipid") String createdByMembershipId,
            @Path("allocatedamount") String allocatedAmount,
            @Path("budgetcategoryid") String budgetCategoryId,
            @Path("allocatedamount_check_1") String allocatedAmountCheck1,
            @Path("allocatedamount_check_2") String allocatedAmountCheck2,
            @Path("allocatedamount_check_3") String allocatedAmountCheck3
    );

    @GET("CreateTaskBudget/{budgetid}/{budgetcategoryid}/{taskid}/{allocatedamount}/{createdbymembershipid}/{taskid_check}/{createdbymembershipid_check}/{budgetcategoryid_check}/{budgetid_check_1}/{budgetid_check_2}/{taskid_exists}/{allocatedamount_check_1}/{allocatedamount_check_2}")
    Call<ResponseBody> createTaskBudget(
            @Path("budgetid") String budgetId,
            @Path("budgetcategoryid") String budgetCategoryId,
            @Path("taskid") String taskId,
            @Path("allocatedamount") String allocatedAmount,
            @Path("createdbymembershipid") String createdByMembershipId,
            @Path("taskid_check") String taskIdCheck,
            @Path("createdbymembershipid_check") String createdByMembershipIdCheck,
            @Path("budgetcategoryid_check") String budgetCategoryIdCheck,
            @Path("budgetid_check_1") String budgetIdCheck1,
            @Path("budgetid_check_2") String budgetIdCheck2,
            @Path("taskid_exists") String taskIdExists,
            @Path("allocatedamount_check_1") String allocatedAmountCheck1,
            @Path("allocatedamount_check_2") String allocatedAmountCheck2
    );

    @GET("UpdateTaskBudgetAllocation/{createdbymembershipid}/{allocatedamount}/{taskbudgetid}/{allocatedamount_check_1}/{allocatedamount_check_2}")
    Call<ResponseBody> updateTaskBudgetAllocation(
            @Path("createdbymembershipid") String createdByMembershipId,
            @Path("allocatedamount") String allocatedAmount,
            @Path("taskbudgetid") String taskBudgetId,
            @Path("allocatedamount_check_1") String allocatedAmountCheck1,
            @Path("allocatedamount_check_2") String allocatedAmountCheck2
    );

    @GET("UpdateTaskBudgetActualCost/{createdbymembershipid}/{actualcostamount}/{taskbudgetid}/{actualcostamount_check}")
    Call<ResponseBody> updateTaskBudgetActualCost(
            @Path("createdbymembershipid") String createdByMembershipId,
            @Path("actualcostamount") String actualCostAmount,
            @Path("taskbudgetid") String taskBudgetId,
            @Path("actualcostamount_check") String actualCostAmountCheck
    );

    @GET("CreateExpense/{expensetitle}/{expenseamount}/{expensedate}/{vendorname}/{notes}/{createdbymembershipid}/{createdbymembershipid_check}/{taskbudgetid}/{expenseamount_check}")
    Call<ResponseBody> createExpense(
            @Path(value = "expensetitle", encoded = true) String expenseTitle,
            @Path("expenseamount") String expenseAmount,
            @Path(value = "expensedate", encoded = true) String expenseDate,
            @Path(value = "vendorname", encoded = true) String vendorName,
            @Path(value = "notes", encoded = true) String notes,
            @Path("createdbymembershipid") String createdByMembershipId,
            @Path("createdbymembershipid_check") String createdByMembershipIdCheck,
            @Path("taskbudgetid") String taskBudgetId,
            @Path("expenseamount_check") String expenseAmountCheck
    );

    @GET("GetTaskBudgetExpenses/{taskbudgetid}")
    Call<List<Expense>> getTaskBudgetExpenses(@Path("taskbudgetid") String taskBudgetId);

    @GET("SyncTaskBudgetActualFromExpenses/{taskbudgetid}/{createdbymembershipid}")
    Call<ResponseBody> syncTaskBudgetActualFromExpenses(
            @Path("taskbudgetid") String taskBudgetId,
            @Path("createdbymembershipid") String createdByMembershipId
    );

    // REST APIs for new dashboard modules.
    @GET("groups/{groupId}/events")
    Call<List<Event>> getGroupEventsRest(@Path("groupId") String groupId);

    @POST("groups/{groupId}/events")
    Call<ResponseBody> createEventRest(
            @Path("groupId") String groupId,
            @Body Map<String, String> body
    );

    @PATCH("events/{eventId}")
    Call<ResponseBody> updateEventRest(
            @Path("eventId") String eventId,
            @Body Map<String, String> body
    );

    @DELETE("events/{eventId}")
    Call<ResponseBody> deleteEventRest(@Path("eventId") String eventId);

    @GET("groups/{groupId}/admin-access-requests")
    Call<List<Request>> getGroupAdminAccessRequestsRest(@Path("groupId") String groupId);

    @GET("groups/{groupId}/admin-access-requests")
    Call<List<Request>> getUserAdminAccessRequestRest(
            @Path("groupId") String groupId,
            @Query("userId") String userId,
            @Query("requestType") String requestType
    );

    @POST("groups/{groupId}/admin-access-requests")
    Call<ResponseBody> createAdminAccessRequestRest(
            @Path("groupId") String groupId,
            @Body Map<String, String> body
    );

    @PATCH("admin-access-requests/{requestId}")
    Call<ResponseBody> updateAdminAccessRequestStatusRest(
            @Path("requestId") String requestId,
            @Body Map<String, String> body
    );

    // Legacy GET-style fallbacks for new modules.
    @GET("GetGroupEvents/{groupid}")
    Call<List<Event>> getGroupEventsLegacy(@Path("groupid") String groupId);

    @GET("CreateEvent/{groupid}/{title}/{description}/{eventdatetime}/{createdbymembershipid}")
    Call<ResponseBody> createEventLegacy(
            @Path("groupid") String groupId,
            @Path(value = "title", encoded = true) String title,
            @Path(value = "description", encoded = true) String description,
            @Path(value = "eventdatetime", encoded = true) String eventDateTime,
            @Path("createdbymembershipid") String createdByMembershipId
    );

    @GET("UpdateEvent/{title}/{description}/{eventdatetime}/{eventid}")
    Call<ResponseBody> updateEventLegacy(
            @Path("eventid") String eventId,
            @Path(value = "title", encoded = true) String title,
            @Path(value = "description", encoded = true) String description,
            @Path(value = "eventdatetime", encoded = true) String eventDateTime
    );

    @GET("DeleteEvent/{eventid}")
    Call<ResponseBody> deleteEventLegacy(@Path("eventid") String eventId);

    @GET("GetGroupAdminAccessRequests/{groupid}")
    Call<List<Request>> getGroupAdminAccessRequestsLegacy(@Path("groupid") String groupId);

    @GET("GetUserAdminAccessRequest/{userid}/{groupid}")
    Call<List<Request>> getUserAdminAccessRequestLegacy(
            @Path("userid") String userId,
            @Path("groupid") String groupId
    );

    // StudEV binds path segments by placeholder occurrence order in the SQL service.
    // This create service reuses :userid and :groupid inside the WHERE NOT EXISTS clause,
    // so the values must be repeated in the URL.
    @GET("CreateAdminAccessRequest/{userid_insert}/{groupid_insert}/{description}/{userid_exists}/{groupid_exists}")
    Call<ResponseBody> createAdminAccessRequestLegacy(
            @Path("userid_insert") String userIdInsert,
            @Path("groupid_insert") String groupIdInsert,
            @Path("description") String description,
            @Path("userid_exists") String userIdExists,
            @Path("groupid_exists") String groupIdExists
    );

    // The update SQL mentions :status before :requestid, so the URL must follow that order.
    @GET("UpdateAdminAccessRequestStatus/{status}/{requestid}")
    Call<ResponseBody> updateAdminAccessRequestStatusLegacy(
            @Path("status") String status,
            @Path("requestid") String requestId
    );
}
