package com.example.studentbroadcastsystem;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  FirebaseManager { 
    
    private final FirebaseFirestore db;
    private static FirebaseManager instance;

    // Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_STUDENTS = "students";
    public static final String COLLECTION_MESSAGES = "messages";

    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    // Callbacks
    public interface LoginCallback {
        void onResult(boolean success);
        void onError(Exception e);
    }

    public interface RegisterCallback {
        void onResult(boolean success);
        void onError(Exception e);
    }

    public interface AddMessageCallback {
        void onResult(boolean success);
        void onError(Exception e);
    }

    public interface FetchMessagesCallback {
        void onMessagesFetched(List<MessageModel> messages);
        void onError(Exception e);
    }

    public interface MessageActionCallback {
        void onSuccess();
        void onError(Exception e);
    }
    
    public interface FetchStudentsCallback {
        void onStudentsFetched(List<String> emails);
        void onError(Exception e);
    }

    // --- Authentication & Faculty Management ---

    public void checkUserLogin(String email, String password, String role, LoginCallback callback) {
        db.collection(COLLECTION_USERS)
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .whereEqualTo("role", role)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        callback.onResult(true);
                    } else {
                        callback.onResult(false);
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    public void registerFaculty(String email, String password, RegisterCallback callback) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);
        user.put("role", "faculty");

        db.collection(COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> callback.onResult(true))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onResult(false);
                });
    }

    // --- Message Management ---

    public void addMessageRequest(String senderEmail, String subject, String message, String branch, String semester, boolean isIndividual, String individualEmail, AddMessageCallback callback) {
        Map<String, Object> msg = new HashMap<>();
        // In Firestore, creating our own integer IDs is hard. 
        // We'll generate a random integer for 'id' to keep it compatible with existing MessageModel structure,
        // OR we change MessageModel to use String IDs. 
        // For simplicity and to not break everything immediately, we'll store a random ID or timestamp.
        int generatedId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        
        msg.put("id", generatedId);
        msg.put("sender_email", senderEmail);
        msg.put("subject", subject);
        msg.put("content", message);
        msg.put("branch", branch);
        msg.put("semester", semester);
        long currentTimestamp = System.currentTimeMillis();
        msg.put("timestamp", currentTimestamp);
        msg.put("is_individual", isIndividual);
        msg.put("individual_email", individualEmail != null ? individualEmail : "");
        msg.put("rejection_reason", "");

        db.collection(COLLECTION_MESSAGES)
                .document(String.valueOf(generatedId))
                .set(msg)
                .addOnSuccessListener(aVoid -> callback.onResult(true))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onResult(false);
                });
    }

    public void getPendingMessages(FetchMessagesCallback callback) {
        db.collection(COLLECTION_MESSAGES)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<MessageModel> messages = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            int id = document.getLong("id").intValue();
                            String sender = document.getString("sender_email");
                            String content = document.getString("content");
                            String branch = document.getString("branch");
                            String semester = document.getString("semester");
                            String status = document.getString("status");
                            boolean isInd = Boolean.TRUE.equals(document.getBoolean("is_individual"));
                            String indEmail = document.getString("individual_email");
                            String rejReason = document.getString("rejection_reason");
                            long timestamp = document.contains("timestamp") ? document.getLong("timestamp") : 0L;
                            String subject = document.getString("subject");
                            if (subject == null) subject = "";
                            messages.add(new MessageModel(id, sender, subject, content, branch, semester, status, isInd, indEmail, rejReason, timestamp));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onMessagesFetched(messages);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getMessageById(int id, FetchMessagesCallback callback) {
        db.collection(COLLECTION_MESSAGES)
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<MessageModel> list = new ArrayList<>();
                    if (documentSnapshot.exists()) {
                        try {
                            int msgId = documentSnapshot.getLong("id").intValue();
                            String sender = documentSnapshot.getString("sender_email");
                            String subject = documentSnapshot.getString("subject");
                            if (subject == null) subject = "";
                            String content = documentSnapshot.getString("content");
                            String branch = documentSnapshot.getString("branch");
                            String semester = documentSnapshot.getString("semester");
                            String status = documentSnapshot.getString("status");
                            boolean isInd = Boolean.TRUE.equals(documentSnapshot.getBoolean("is_individual"));
                            String indEmail = documentSnapshot.getString("individual_email");
                            String rejReason = documentSnapshot.getString("rejection_reason");
                            long timestamp = documentSnapshot.contains("timestamp") ? documentSnapshot.getLong("timestamp") : 0L;
                            list.add(new MessageModel(msgId, sender, subject, content, branch, semester, status, isInd, indEmail, rejReason, timestamp));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onMessagesFetched(list);
                })
                .addOnFailureListener(callback::onError);
    }

    public void approveMessage(int messageId, MessageActionCallback callback) {
        db.collection(COLLECTION_MESSAGES)
                .document(String.valueOf(messageId))
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public void rejectMessage(int messageId, String reason, MessageActionCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "rejected");
        updates.put("rejection_reason", reason != null ? reason : "");
        db.collection(COLLECTION_MESSAGES)
                .document(String.valueOf(messageId))
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public void updateMessage(MessageModel msg, MessageActionCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("subject", msg.getSubject());
        updates.put("content", msg.getContent());
        updates.put("branch", msg.getBranch());
        updates.put("semester", msg.getSemester());
        db.collection(COLLECTION_MESSAGES)
                .document(String.valueOf(msg.getId()))
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public void getMessagesBySender(String senderEmail, FetchMessagesCallback callback) {
        db.collection(COLLECTION_MESSAGES)
                .whereEqualTo("sender_email", senderEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<MessageModel> messages = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            int id = document.getLong("id").intValue();
                            String sender = document.getString("sender_email");
                            String content = document.getString("content");
                            String branch = document.getString("branch");
                            String semester = document.getString("semester");
                            String status = document.getString("status");
                            boolean isInd = Boolean.TRUE.equals(document.getBoolean("is_individual"));
                            String indEmail = document.getString("individual_email");
                            String rejReason = document.getString("rejection_reason");
                            long timestamp = document.contains("timestamp") ? document.getLong("timestamp") : 0L;
                            String subject = document.getString("subject");
                            if (subject == null) subject = "";
                            messages.add(new MessageModel(id, sender, subject, content, branch, semester, status, isInd, indEmail, rejReason, timestamp));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onMessagesFetched(messages);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getProcessedMessages(FetchMessagesCallback callback) {
        db.collection(COLLECTION_MESSAGES)
                .whereNotEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<MessageModel> messages = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            int id = document.getLong("id").intValue();
                            String sender = document.getString("sender_email");
                            String content = document.getString("content");
                            String branch = document.getString("branch");
                            String semester = document.getString("semester");
                            String status = document.getString("status");
                            boolean isInd = Boolean.TRUE.equals(document.getBoolean("is_individual"));
                            String indEmail = document.getString("individual_email");
                            String rejReason = document.getString("rejection_reason");
                            long timestamp = document.contains("timestamp") ? document.getLong("timestamp") : 0L;
                            String subject = document.getString("subject");
                            if (subject == null) subject = "";
                            messages.add(new MessageModel(id, sender, subject, content, branch, semester, status, isInd, indEmail, rejReason, timestamp));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onMessagesFetched(messages);
                })
                .addOnFailureListener(callback::onError);
    }

    // --- Student Management ---

    public void getStudentEmails(String branchStr, String semesterStr, FetchStudentsCallback callback) {
        db.collection(COLLECTION_STUDENTS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> validBranches = Arrays.asList(branchStr.split("\\s*,\\s*"));
                    List<String> validSemesters = Arrays.asList(semesterStr.split("\\s*,\\s*"));
                    List<String> emails = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String b = document.getString("branch");
                        String s = document.getString("semester");
                        if (b != null && s != null && validBranches.contains(b) && validSemesters.contains(s)) {
                            String email = document.getString("email");
                            if (email != null) {
                                emails.add(email);
                            }
                        }
                    }
                    callback.onStudentsFetched(emails);
                })
                .addOnFailureListener(callback::onError);
    }
    
    // Developer tool to populate initial database in Firestore
    public void seedInitialData(MessageActionCallback callback) {
        // Users
        Map<String, Object> admin = new HashMap<>();
        admin.put("email", "Admin.admin240@gmail.com");
        admin.put("password", "Admin@123");
        admin.put("role", "admin");
        db.collection(COLLECTION_USERS).add(admin);

        Map<String, Object> fac1 = new HashMap<>();
        fac1.put("email", "24karmorep@rbunagpur.in");
        fac1.put("password", "Karmore@123");
        fac1.put("role", "faculty");
        db.collection(COLLECTION_USERS).add(fac1);
        
        Map<String, Object> fac2 = new HashMap<>();
        fac2.put("email", "24deshmukhs@rbunagpur.in");
        fac2.put("password", "deshmukh@321");
        fac2.put("role", "faculty");
        db.collection(COLLECTION_USERS).add(fac2);

        // Students
        addStudent("ankitgujare008@gmail.com", "BCA", "4");
        addStudent("student1_bca4@demo.com", "BCA", "4");
        addStudent("student2_bca4@demo.com", "BCA", "4");
        addStudent("student1_mca2@demo.com", "MCA", "2");
        addStudent("student1_bca1@demo.com", "BCA", "1");
        
        // MCA Sem 1
        addStudent("gojosaturo8380@gmail.com", "MCA", "1");
        addStudent("aashikaj1803@gmail.com", "MCA", "1");
        addStudent("24guptav@rbunagpur.in", "MCA", "1");
        addStudent("minalparate605@gmail.com", "MCA", "1");
        addStudent("minalparate607@gmail.com", "MCA", "1");

        // MCA Sem 2
        addStudent("24kharwades@rbunagpur.in", "MCA", "2");
        addStudent("24kanojiyak@rbunagpur.in", "MCA", "2");
        addStudent("24gadekars@rbunagpur.in", "MCA", "2");
        addStudent("24naikd@rbunagpur.in", "MCA", "2");

        // MCA Sem 3
        addStudent("24labded@rbunagpur.in", "MCA", "3");
        addStudent("24paratem@rbunagpur.in", "MCA", "3");
        addStudent("24hedaop@rbunagpur.in", "MCA", "3");
        addStudent("24bhakrej@rbunagpur.in", "MCA", "3");
        addStudent("dhanashree2528@gmail.com", "MCA", "3");

        // MCA Sem 4
        addStudent("24madavim@rbunagpur.in", "MCA", "4");
        addStudent("dhanashree343@gmail.com", "MCA", "4");
        addStudent("mmadavi8380@gmail.com", "MCA", "4");
        addStudent("minalparate606@gmail.com", "MCA", "4");

        // BCA Sem 1
        addStudent("24khergader@rbunagpur.in", "BCA", "1");
        addStudent("24muleyp@rbunagpur.in", "BCA", "1");
        addStudent("24sahum@rbunagpur.in", "BCA", "1");

        // BCA Sem 2
        addStudent("24tiwaris@rbunagpur.in", "BCA", "2");
        addStudent("24yadavv@rbunagpur.in", "BCA", "2");
        addStudent("24rauta@rbunagpur.in", "BCA", "2");

        // BCA Sem 3
        addStudent("24nandanwarc@rbunagpur.in", "BCA", "3");
        addStudent("24singh@rbunagpur.in", "BCA", "3");

        // BCA Sem 4
        addStudent("24jaina@rbunagpur.in", "BCA", "4");
        addStudent("24thakurc@rbunagpur.in", "BCA", "4");
        addStudent("mitalimadavi7218@gmail.com", "BCA", "4");
        addStudent("24bantep@rbunagpur.in", "BCA", "4");
        addStudent("ankitgujare36@gmail.com", "BCA", "4");
        
        callback.onSuccess();
    }
    
    private void addStudent(String email, String branch, String semester) {
        Map<String, Object> std = new HashMap<>();
        std.put("email", email);
        std.put("branch", branch);
        std.put("semester", semester);
        db.collection(COLLECTION_STUDENTS).add(std);
    }
}
