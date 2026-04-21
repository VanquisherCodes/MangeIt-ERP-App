package com.example.manageit.network;

import com.example.manageit.models.Announcement;
import com.example.manageit.models.GroupMember;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.StudentGroup;
import com.example.manageit.models.Task;
import com.example.manageit.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


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
}
