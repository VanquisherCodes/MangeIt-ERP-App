package com.example.manageit.network;

import com.example.manageit.models.Announcement;
import com.example.manageit.models.Event;
import com.example.manageit.models.GroupMember;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Request;
import com.example.manageit.models.StudentGroup;
import com.example.manageit.models.Task;
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
            @Path("title") String title,
            @Path("message") String message,
            @Path("createdbymembershipid") String createdByMembershipId
    );

    @GET("GetGroupMembers/{groupid}")
    Call<List<GroupMember>> getGroupMembers(@Path("groupid") String groupId);

    @GET("UpdateMemberRole/{membershipid}/{roleingroup}")
    Call<ResponseBody> updateMemberRole(
            @Path("membershipid") String membershipId,
            @Path("roleingroup") String roleInGroup
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
            @Path("title") String title,
            @Path("description") String description,
            @Path("eventdatetime") String eventDateTime,
            @Path("createdbymembershipid") String createdByMembershipId
    );

    @GET("UpdateEvent/{eventid}/{title}/{description}/{eventdatetime}")
    Call<ResponseBody> updateEventLegacy(
            @Path("eventid") String eventId,
            @Path("title") String title,
            @Path("description") String description,
            @Path("eventdatetime") String eventDateTime
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
