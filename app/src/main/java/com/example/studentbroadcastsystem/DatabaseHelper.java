package com.example.studentbroadcastsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BroadcastSystem.db";
    private static final int DATABASE_VERSION = 4;

    // Users Table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ROLE = "role"; // 'admin' or 'faculty'

    // Students Table
    public static final String TABLE_STUDENTS = "students";
    public static final String COL_STUDENT_ID = "id";
    public static final String COL_STUDENT_EMAIL = "email";
    public static final String COL_STUDENT_BRANCH = "branch";
    public static final String COL_STUDENT_SEMESTER = "semester";

    // Messages Table
    public static final String TABLE_MESSAGES = "messages";
    public static final String COL_MSG_ID = "id";
    public static final String COL_MSG_SENDER = "sender_email";
    public static final String COL_MSG_CONTENT = "content";
    public static final String COL_MSG_BRANCH = "branch";
    public static final String COL_MSG_SEMESTER = "semester";
    public static final String COL_MSG_STATUS = "status"; // 'pending', 'approved', 'rejected'
    public static final String COL_MSG_IS_INDIVIDUAL = "is_individual"; // 1 or 0
    public static final String COL_MSG_INDIVIDUAL_EMAIL = "individual_email";
    public static final String COL_MSG_REJECTION_REASON = "rejection_reason";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_ROLE + " TEXT)";
        
        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + " (" +
                COL_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_STUDENT_EMAIL + " TEXT, " +
                COL_STUDENT_BRANCH + " TEXT, " +
                COL_STUDENT_SEMESTER + " TEXT)";
                
        String createMessagesTable = "CREATE TABLE " + TABLE_MESSAGES + " (" +
                COL_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MSG_SENDER + " TEXT, " +
                COL_MSG_CONTENT + " TEXT, " +
                COL_MSG_BRANCH + " TEXT, " +
                COL_MSG_SEMESTER + " TEXT, " +
                COL_MSG_STATUS + " TEXT, " +
                COL_MSG_IS_INDIVIDUAL + " INTEGER, " +
                COL_MSG_INDIVIDUAL_EMAIL + " TEXT, " +
                COL_MSG_REJECTION_REASON + " TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createStudentsTable);
        db.execSQL(createMessagesTable);
        
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Add Admin
        ContentValues adminVars = new ContentValues();
        adminVars.put(COL_USER_EMAIL, "Admin.admin240@gmail.com");
        adminVars.put(COL_USER_PASSWORD, "Admin@123");
        adminVars.put(COL_USER_ROLE, "admin");
        db.insert(TABLE_USERS, null, adminVars);

        // Add Faculty predefined credentials
        ContentValues fac1 = new ContentValues();
        fac1.put(COL_USER_EMAIL, "24karmorep@rbunagpur.in");
        fac1.put(COL_USER_PASSWORD, "Karmore@123");
        fac1.put(COL_USER_ROLE, "faculty");
        db.insert(TABLE_USERS, null, fac1);
        
        ContentValues fac2 = new ContentValues();
        fac2.put(COL_USER_EMAIL, "24deshmukhs@rbunagpur.in");
        fac2.put(COL_USER_PASSWORD, "deshmukh@321");
        fac2.put(COL_USER_ROLE, "faculty");
        db.insert(TABLE_USERS, null, fac2);

        // Add Test Students
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('ankitgujare008@gmail.com', 'BCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('student1_bca4@demo.com', 'BCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('student2_bca4@demo.com', 'BCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('student1_mca2@demo.com', 'MCA', '2')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('student1_bca1@demo.com', 'BCA', '1')");

        // MCA Sem 1
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('gojosaturo8380@gmail.com', 'MCA', '1')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('aashikaj1803@gmail.com', 'MCA', '1')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24guptav@rbunagpur.in', 'MCA', '1')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('minalparate605@gmail.com', 'MCA', '1')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('minalparate607@gmail.com', 'MCA', '1')");

        // MCA Sem 2
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24kharwades@rbunagpur.in', 'MCA', '2')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24kanojiyak@rbunagpur.in', 'MCA', '2')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24gadekars@rbunagpur.in', 'MCA', '2')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24naikd@rbunagpur.in', 'MCA', '2')");

        // MCA Sem 3
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24labded@rbunagpur.in', 'MCA', '3')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24paratem@rbunagpur.in', 'MCA', '3')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24hedaop@rbunagpur.in', 'MCA', '3')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24bhakrej@rbunagpur.in', 'MCA', '3')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('dhanashree2528@gmail.com', 'MCA', '3')");

        // MCA Sem 4
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24madavim@rbunagpur.in', 'MCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('dhanashree343@gmail.com', 'MCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('mmadavi8380@gmail.com', 'MCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('minalparate606@gmail.com', 'MCA', '4')");

        // BCA Sem 1
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24khergader@rbunagpur.in', 'BCA', '1')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24muleyp@rbunagpur.in', 'BCA', '1')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24sahum@rbunagpur.in', 'BCA', '1')");

        // BCA Sem 2
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24tiwaris@rbunagpur.in', 'BCA', '2')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24yadavv@rbunagpur.in', 'BCA', '2')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24rauta@rbunagpur.in', 'BCA', '2')");

        // BCA Sem 3
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24nandanwarc@rbunagpur.in', 'BCA', '3')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24singh@rbunagpur.in', 'BCA', '3')");

        // BCA Sem 4
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24jaina@rbunagpur.in', 'BCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24thakurc@rbunagpur.in', 'BCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('mitalimadavi7218@gmail.com', 'BCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('24bantep@rbunagpur.in', 'BCA', '4')");
        db.execSQL("INSERT INTO " + TABLE_STUDENTS + " (email, branch, semester) VALUES ('ankitgujare36@gmail.com', 'BCA', '4')");
    }

    // --- Authentication & Faculty Management ---

    public boolean checkUserLogin(String email, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=? AND role=?", new String[]{email, password, role});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean registerFaculty(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_USER_PASSWORD, password);
        cv.put(COL_USER_ROLE, "faculty");
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    // --- Message Management ---

    public boolean addMessageRequest(String senderEmail, String message, String branch, String semester, boolean isIndividual, String individualEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MSG_SENDER, senderEmail);
        cv.put(COL_MSG_CONTENT, message);
        cv.put(COL_MSG_BRANCH, branch);
        cv.put(COL_MSG_SEMESTER, semester);
        cv.put(COL_MSG_STATUS, "pending");
        cv.put(COL_MSG_IS_INDIVIDUAL, isIndividual ? 1 : 0);
        cv.put(COL_MSG_INDIVIDUAL_EMAIL, individualEmail != null ? individualEmail : "");
        cv.put(COL_MSG_REJECTION_REASON, "");
        long result = db.insert(TABLE_MESSAGES, null, cv);
        return result != -1;
    }

    public List<MessageModel> getPendingMessages() {
        List<MessageModel> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " WHERE status='pending'", null);
        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_ID));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_SENDER));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_CONTENT));
                String branch = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_BRANCH));
                String semester = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_SEMESTER));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_STATUS));
                boolean isInd = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_IS_INDIVIDUAL)) == 1;
                String indEmail = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_INDIVIDUAL_EMAIL));
                String rejReason = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_REJECTION_REASON));
                messages.add(new MessageModel(id, sender, content, branch, semester, status, isInd, indEmail, rejReason));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }

    public MessageModel getMessageById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " WHERE id=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            int msgId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_ID));
            String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_SENDER));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_CONTENT));
            String branch = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_BRANCH));
            String semester = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_SEMESTER));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_STATUS));
            boolean isInd = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_IS_INDIVIDUAL)) == 1;
            String indEmail = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_INDIVIDUAL_EMAIL));
            String rejReason = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_REJECTION_REASON));
            cursor.close();
            return new MessageModel(msgId, sender, content, branch, semester, status, isInd, indEmail, rejReason);
        }
        cursor.close();
        return null; // Not found
    }

    public void approveMessage(int messageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MSG_STATUS, "approved");
        db.update(TABLE_MESSAGES, cv, "id=?", new String[]{String.valueOf(messageId)});
    }

    public void rejectMessage(int messageId, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MSG_STATUS, "rejected");
        cv.put(COL_MSG_REJECTION_REASON, reason);
        db.update(TABLE_MESSAGES, cv, "id=?", new String[]{String.valueOf(messageId)});
    }

    public void updateMessage(MessageModel msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MSG_CONTENT, msg.getContent());
        cv.put(COL_MSG_BRANCH, msg.getBranch());
        cv.put(COL_MSG_SEMESTER, msg.getSemester());
        db.update(TABLE_MESSAGES, cv, "id=?", new String[]{String.valueOf(msg.getId())});
    }

    public List<MessageModel> getMessagesBySender(String senderEmail) {
        List<MessageModel> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGES + " WHERE sender_email=?", new String[]{senderEmail});
        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_ID));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_SENDER));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_CONTENT));
                String branch = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_BRANCH));
                String semester = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_SEMESTER));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_STATUS));
                boolean isInd = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_IS_INDIVIDUAL)) == 1;
                String indEmail = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_INDIVIDUAL_EMAIL));
                String rejReason = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_REJECTION_REASON));
                messages.add(new MessageModel(id, sender, content, branch, semester, status, isInd, indEmail, rejReason));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }

    // --- Student Management ---

    public List<String> getStudentEmails(String branch, String semester) {
        List<String> emails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM " + TABLE_STUDENTS + " WHERE branch=? AND semester=?", new String[]{branch, semester});
        if(cursor.moveToFirst()) {
            do {
                emails.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return emails;
    }
}
