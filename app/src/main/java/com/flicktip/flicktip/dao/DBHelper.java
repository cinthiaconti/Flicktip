package com.flicktip.flicktip.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flicktip.flicktip.model.Award;
import com.flicktip.flicktip.model.Category;
import com.flicktip.flicktip.model.Edition;
import com.flicktip.flicktip.model.Movie;
import com.flicktip.flicktip.model.Nomination;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by Cinthia on 07/02/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 24;
    private static final String DATABASE = "Flicktip";
    private static DBHelper sInstance;
    private final Context context;

    //TABLE NAMES
    private static final String TABLE_AWARD = "award";
    private static final String TABLE_EDITION = "edition";
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_EDITION_CATEGORY = "edition_category";
    private static final String TABLE_MOVIE = "movie";
    private static final String TABLE_NOMINEE = "nominee";
    private static final String TABLE_NOMINATION = "nomination";
    private static final String TABLE_NOMINEE_NOMINATION = "nominee_nomination";

    //COLUMN NAMES
    private static final String KEY_ID = "id";
    private static final String KEY_ID_AWARD = "id_award";
    private static final String KEY_ID_EDITION = "id_edition";
    private static final String KEY_ID_CATEGORY = "id_category";
    private static final String KEY_ID_MOVIE = "id_movie";
    private static final String KEY_ID_NOMINEE = "id_nominee";
    private static final String KEY_ID_NOMINATION = "id_nomination";

    public static final String KEY_VIEWED = "viewed";
    public static final String KEY_FAVORITE = "favorite";
    public static final String KEY_BOOKMARK = "bookmark";

    private static final String KEY_NAME = "name";
    private static final String KEY_PT_BR = "pt_br";
    private static final String KEY_WINNER = "winner";
    private static final String KEY_YEAR = "year";

    private DBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
        this.context = context;
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOMINEE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AWARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDITION_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOMINATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOMINEE_NOMINATION);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql_award = "CREATE TABLE " + TABLE_AWARD + " (" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT NOT NULL)";

        String sql_edition = "CREATE TABLE " + TABLE_EDITION + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_NAME + " TEXT NOT NULL, " +
                KEY_ID_AWARD + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + KEY_ID_AWARD + ") REFERENCES " + TABLE_AWARD + "(" + KEY_ID + "))";

        String sql_category = "CREATE TABLE " + TABLE_CATEGORY + " (" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_PT_BR + " TEXT NOT NULL, " +
                KEY_NAME + " TEXT NOT NULL)";

        String sql_movie = "CREATE TABLE " + TABLE_MOVIE + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_NAME + " TEXT NOT NULL, " +
                KEY_YEAR + " TEXT NOT NULL, " +
                KEY_FAVORITE + " INTEGER, " +
                KEY_BOOKMARK + " INTEGER, " +
                KEY_VIEWED + " INTEGER, " +
                KEY_ID_EDITION + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + KEY_ID_EDITION + ") REFERENCES " + TABLE_EDITION + "(" + KEY_ID + ")) ";

        String sql_nominee = "CREATE TABLE " + TABLE_NOMINEE + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_NAME + " TEXT NOT NULL)";

        String sql_nomination = "CREATE TABLE " + TABLE_NOMINATION + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_WINNER + " INTEGER, " +
                KEY_ID_EDITION + " INTEGER NOT NULL, " +
                KEY_ID_CATEGORY + " INTEGER NOT NULL, " +
                KEY_ID_MOVIE + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + KEY_ID_MOVIE + ") REFERENCES " + TABLE_MOVIE + "(" + KEY_ID + "), " +
                " FOREIGN KEY (" + KEY_ID_EDITION + ") REFERENCES " + TABLE_EDITION_CATEGORY + "(" + KEY_ID_EDITION + "), " +
                " FOREIGN KEY (" + KEY_ID_CATEGORY + ") REFERENCES " + TABLE_EDITION_CATEGORY + "(" + KEY_ID_CATEGORY + "))";

        String sql_nominee_nomination = "CREATE TABLE " + TABLE_NOMINEE_NOMINATION + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_ID_NOMINATION + " INTEGER, " +
                KEY_ID_NOMINEE + " INTEGER," +
                " FOREIGN KEY (" + KEY_ID_NOMINATION + ") REFERENCES " + TABLE_NOMINATION + "(" + KEY_ID + "), " +
                " FOREIGN KEY (" + KEY_ID_NOMINEE + ") REFERENCES " + TABLE_NOMINEE + "(" + KEY_ID + "))";

        String sql_edition_category = "CREATE TABLE " + TABLE_EDITION_CATEGORY + " (" +
                KEY_ID_EDITION + " INTEGER NOT NULL, " +
                KEY_ID_CATEGORY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + KEY_ID_EDITION + ") REFERENCES " + TABLE_EDITION + "(" + KEY_ID + "), " +
                " FOREIGN KEY (" + KEY_ID_CATEGORY + ") REFERENCES " + TABLE_CATEGORY + "(" + KEY_ID + "))";

        db.execSQL(sql_award);
        db.execSQL(sql_edition);
        db.execSQL(sql_movie);
        db.execSQL(sql_category);
        db.execSQL(sql_nominee);
        db.execSQL(sql_nomination);
        db.execSQL(sql_nominee_nomination);
        db.execSQL(sql_edition_category);
    }

    private boolean isMasterEmpty() {

        boolean flag;
        String quString = "select exists(select 1 from " + TABLE_AWARD + ");";

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(quString, null);
        c.moveToFirst();

        flag = c.getInt(0) != 1;

        c.close();
        db.close();

        return flag;
    }

    private Long checkIfExists(String table, String data) {

        data = data.replace("'", "''");

        Long id = null;
        String sql = "SELECT id FROM " + table + " WHERE " + KEY_NAME + " LIKE('" + data + "')";
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
        }
        cursor.close();
        return id;
    }

    public void populateDB() {

        if (isMasterEmpty()) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, "Oscars®");
            getWritableDatabase().insert(TABLE_AWARD, null, values);

            populateCategory();

            try {
                AssetManager am = context.getAssets();
                InputStream is = am.open("oscar.xls");
                Workbook wb = Workbook.getWorkbook(is);

                Sheet s = wb.getSheet(0);
                int row = s.getRows();
                int col = s.getColumns();


                for (int i = 0; i < row; i++) {
                    List<String> item = new ArrayList<>();

                    for (int c = 0; c < col; c++) {
                        Cell z = s.getCell(c, i);
                        String content = z.getContents();
                        item.add(content);
                    }

                    System.out.println(item);

                    Long idCategory = checkIfExists(TABLE_CATEGORY, item.get(1));
                    Long idEdition = populateEdition((long) 1, item.get(0));
                    Long idMovie = populateMovie(idEdition, item.get(2), item.get(3));
                    Long idNominee = populateNominee(item.get(4));

                    String winner = item.get(5);


                    Long idNomination = populateNomination(idEdition, idCategory, idMovie, winner);

                    populateEditionCategory(idEdition, idCategory);
                    populateNomineeNomination(idNominee, idNomination);

                    item.clear();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Long populateEdition(Long idAward, String edition) {

        Long id = checkIfExists(DBHelper.TABLE_EDITION, edition);

        if (id == null) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, edition);
            values.put(KEY_ID_AWARD, idAward);
            id = getWritableDatabase().insert(DBHelper.TABLE_EDITION, null, values);
        }
        return id;
    }

    private void populateCategory() {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("categories.txt")));

            String line = br.readLine();

            while (line != null) {

                System.out.println(line);

                String[] parts = line.split("#");
                Long id = checkIfExists(DBHelper.TABLE_CATEGORY, parts[0]);

                if (id == null) {
                    ContentValues values = new ContentValues();
                    values.put(KEY_NAME, parts[0]);
                    values.put(KEY_PT_BR, parts[1]);

                    getWritableDatabase().insert(DBHelper.TABLE_CATEGORY, null, values);
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateEditionCategory(Long idEdition, Long idCategory) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID_CATEGORY, idCategory);
        values.put(KEY_ID_EDITION, idEdition);
        getWritableDatabase().insert(TABLE_EDITION_CATEGORY, null, values);
    }

    private Long populateMovie(Long idEdition, String movie, String year) {

        Long id = checkIfExists(DBHelper.TABLE_MOVIE, movie);

        if (id == null) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, movie);
            values.put(KEY_YEAR, year);
            values.put(KEY_ID_EDITION, idEdition);
            id = getWritableDatabase().insert(DBHelper.TABLE_MOVIE, null, values);
        }
        return id;
    }

    private Long populateNominee(String nominee) {

        Long id = checkIfExists(DBHelper.TABLE_NOMINEE, nominee);
        if (id == null) {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, nominee);
            id = getWritableDatabase().insert(DBHelper.TABLE_NOMINEE, null, values);
        }
        return id;
    }

    private Long populateNomination(Long idEdition, Long idCategory, Long idMovie, String winner) {

        ContentValues values = new ContentValues();
        values.put(KEY_WINNER, winner);
        values.put(KEY_ID_EDITION, idEdition);
        values.put(KEY_ID_CATEGORY, idCategory);
        values.put(KEY_ID_MOVIE, idMovie);
        return getWritableDatabase().insert(TABLE_NOMINATION, null, values);
    }

    private void populateNomineeNomination(Long idNominee, Long idNomination) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID_NOMINATION, idNomination);
        values.put(KEY_ID_NOMINEE, idNominee);
        getWritableDatabase().insert(TABLE_NOMINEE_NOMINATION, null, values);
    }

    public List<Award> getListAwards() {

        String sql = "SELECT * FROM " + TABLE_AWARD;
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        List<Award> awards = new ArrayList<>();

        while (c.moveToNext()) {

            Award award = new Award();
            award.setId(c.getLong(c.getColumnIndex("id")));
            award.setName(c.getString(c.getColumnIndex("name")));
            awards.add(award);
        }
        c.close();
        return awards;
    }

    public List<Award> getListAwardsByMovie(Movie movie) {

        List<Award> list = new ArrayList<>();

        String sql = "SELECT DISTINCT a.id, a.name " +
                " FROM " + TABLE_AWARD + " a, " + TABLE_EDITION + " e, " + TABLE_EDITION_CATEGORY + " ec, " + TABLE_NOMINATION + " n" +
                " WHERE a.id = e.id_award " +
                " AND e.id = ec.id_edition " +
                " AND ec.id_edition = n.id_edition " +
                " AND n.id_movie = " + movie.getId();

        Cursor c = getReadableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {

            Award item = new Award();
            item.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            item.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            list.add(item);
        }
        c.close();
        return list;
    }

    public List<Category> getListCategories(Edition edition) {

        String name = KEY_NAME;
        if (Locale.getDefault().toString().equals("pt_BR")) {
            name = KEY_PT_BR;
        }

        String sql = "SELECT DISTINCT c.id, c." + name + " FROM " +
                TABLE_CATEGORY + " c, " +
                TABLE_EDITION_CATEGORY + " ec" +
                " WHERE c." + KEY_ID + " = ec." + KEY_ID_CATEGORY +
                " AND ec." + KEY_ID_EDITION + " = " + edition.getId();
        // " ORDER BY (c." + KEY_NAME + ")";

        List<Category> categories = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {
            Category category = new Category();
            category.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            category.setName(c.getString(c.getColumnIndex(name)));
            category.setProgress(getCategoryProgress(edition.getId(), category.getId()));
            categories.add(category);
        }

        c.close();
        return categories;
    }

    public List<Nomination> getListNominationsByMovie(Award award, Movie movie) {

        String name = KEY_NAME;
        if (Locale.getDefault().toString().equals("pt_BR")) {
            name = KEY_PT_BR;
        }

        String sql = "SELECT DISTINCT n.name as n_name, m.name as m_name, n.id as n_id, m.id as m_id, c.id as c_id, c." + name + " as c_name, * " +
                " FROM " +
                TABLE_NOMINEE + " n, " +
                TABLE_NOMINATION + " no, " +
                TABLE_NOMINEE_NOMINATION + " nn, " +
                TABLE_CATEGORY + " c, " +
                TABLE_EDITION + " e, " +
                TABLE_EDITION_CATEGORY + " ec, " +
                TABLE_MOVIE + " m " +

                " WHERE nn." + KEY_ID_NOMINEE + " = n." + KEY_ID +
                " AND nn." + KEY_ID_NOMINATION + " = no." + KEY_ID +
                " AND ec." + KEY_ID_CATEGORY + " = c." + KEY_ID +
                " AND no." + KEY_ID_CATEGORY + " = ec." + KEY_ID_CATEGORY +
                " AND ec." + KEY_ID_EDITION + " = e." + KEY_ID +
                " AND no." + KEY_ID_EDITION + " = ec." + KEY_ID_EDITION +
                " AND no." + KEY_ID_MOVIE + " = m." + KEY_ID +
                " AND no." + KEY_ID_MOVIE + " = " + movie.getId() +
                " AND e." + KEY_ID_AWARD + " = " + award.getId() +
                " ORDER BY (m." + KEY_NAME + ")";

        List<Nomination> nominations = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {

            Nomination nomination = new Nomination();
            nomination.setId(c.getLong(c.getColumnIndex("n_" + KEY_ID)));
            nomination.setName(c.getString(c.getColumnIndex("n_" + KEY_NAME)));

            Category category = new Category();
            category.setId(c.getLong(c.getColumnIndex("c_" + KEY_ID)));
            category.setName(c.getString(c.getColumnIndex("c_" + KEY_NAME)));
            nomination.setCategory(category);
            nomination.setMovie(movie);

            if (c.getInt(c.getColumnIndex(KEY_WINNER)) != 0) {
                nomination.setWinner(true);
            } else {
                nomination.setWinner(false);
            }

            nominations.add(nomination);
        }
        c.close();
        return nominations;
    }

    public List<Nomination> getListNominations(Award award, Edition edition, Category category) {

        String sql = "SELECT DISTINCT m.name as m_name, n.name as n_name, n.id as n_id, m.id as m_id, *" +
                " FROM " +
                TABLE_NOMINEE + " n, " +
                TABLE_NOMINATION + " no, " +
                TABLE_NOMINEE_NOMINATION + " nn, " +
                TABLE_MOVIE + " m, " +
                TABLE_EDITION + " e " +

                " WHERE nn." + KEY_ID_NOMINEE + " = n." + KEY_ID +
                " AND nn." + KEY_ID_NOMINATION + " = no." + KEY_ID +
                " AND no." + KEY_ID_MOVIE + " = m." + KEY_ID +
                " AND no." + KEY_ID_EDITION + " = e." + KEY_ID +
                " AND no." + KEY_ID_CATEGORY + " = " + category.getId() +
                " AND  e." + KEY_ID + " = " + edition.getId() +
                " AND  e." + KEY_ID_AWARD + " = " + award.getId() +
                " ORDER BY (m." + KEY_NAME + ")";

        List<Nomination> nominations = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {

            Nomination nomination = new Nomination();
            nomination.setId(c.getLong(c.getColumnIndex("n_" + KEY_ID)));
            nomination.setName(c.getString(c.getColumnIndex("n_" + KEY_NAME)));

            if (c.getInt(c.getColumnIndex(KEY_WINNER)) != 0) {
                nomination.setWinner(true);
            } else {
                nomination.setWinner(false);
            }

            Movie movie = new Movie();
            movie.setDbTitle(c.getString(c.getColumnIndex("m_" + KEY_NAME)));
            movie.setId(c.getLong(c.getColumnIndex("m_" + KEY_ID)));
            movie.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
            movie.setViewed(c.getInt(c.getColumnIndex(KEY_VIEWED)));
            movie.setFavorite(c.getInt(c.getColumnIndex(KEY_FAVORITE)));
            movie.setBookmark(c.getInt(c.getColumnIndex(KEY_BOOKMARK)));
            nomination.setMovie(movie);
            nominations.add(nomination);
        }
        c.close();
        return nominations;
    }

    public List<Edition> getListEditions(Award award) {

        List<Edition> editions = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_EDITION +
                " WHERE " + KEY_ID_AWARD + " = " + award.getId();

        Cursor c = getReadableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {
            Edition edition = new Edition();

            edition.setId(c.getLong(c.getColumnIndex(DBHelper.KEY_ID)));
            edition.setName(c.getString(c.getColumnIndex(DBHelper.KEY_NAME)));
            edition.setProgress(getEditionProgress(edition.getId()));
            editions.add(edition);
        }
        c.close();
        return editions;
    }

    public List<Integer> getListSize() {

        List<Integer> status = new ArrayList<>();

        String sql = "SELECT" +
                " sum(" + KEY_VIEWED + " = '1') " + KEY_VIEWED + "," +
                " sum(" + KEY_BOOKMARK + " = '1') " + KEY_BOOKMARK + ", " +
                " sum(" + KEY_FAVORITE + " = '1') " + KEY_FAVORITE +
                " FROM " + TABLE_MOVIE;

        Cursor c = getReadableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {
            status.add(0, c.getInt(c.getColumnIndex(KEY_VIEWED)));
            status.add(1, c.getInt(c.getColumnIndex(KEY_BOOKMARK)));
            status.add(2, c.getInt(c.getColumnIndex(KEY_FAVORITE)));
        }
        c.close();
        return status;
    }

    private List<Integer> getEditionProgress(Long idEdition) {

        List<Integer> progress = new ArrayList<>();

        String sql = "SELECT " +
                " sum(" + KEY_VIEWED + " = '1') " + KEY_VIEWED + "," +
                " count(m." + KEY_ID + ") all_movies " +
                " FROM " + TABLE_MOVIE + " m " +
                " WHERE m." + KEY_ID_EDITION + " = " + idEdition;

        Cursor c = getReadableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {
            progress.add(0, c.getInt(c.getColumnIndex(KEY_VIEWED)));
            progress.add(1, c.getInt(c.getColumnIndex("all_movies")));

        }
        c.close();
        return progress;
    }

    private List<Integer> getCategoryProgress(Long idEdition, Long idCategory) {

        List<Integer> progress = new ArrayList<>();

        String sql = "SELECT " +
                " sum(" + KEY_VIEWED + " = '1') " + KEY_VIEWED + "," +
                " count(m." + KEY_ID + ") all_movies " +
                " FROM " + TABLE_MOVIE + " m, " + TABLE_NOMINATION + " n" +
                " WHERE m." + KEY_ID_EDITION + " = " + idEdition +
                " AND m." + KEY_ID + " = n." + KEY_ID_MOVIE +
                " AND n." + KEY_ID_EDITION + " = " + idEdition +
                " AND n." + KEY_ID_CATEGORY + " = " + idCategory;

        Cursor c = getReadableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {
            progress.add(0, c.getInt(c.getColumnIndex(KEY_VIEWED)));
            progress.add(1, c.getInt(c.getColumnIndex("all_movies")));
        }
        c.close();
        return progress;
    }

    public List<Movie> getListMovies(String list) {

        List<Movie> movies = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_MOVIE +
                " WHERE " + list + " = 1";

        Cursor c = getReadableDatabase().rawQuery(sql, null);

        while (c.moveToNext()) {
            Movie movie = new Movie();
            movie.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            movie.setDbTitle(c.getString(c.getColumnIndex(KEY_NAME)));
            movie.setViewed(c.getInt(c.getColumnIndex(KEY_VIEWED)));
            movie.setYear(c.getString(c.getColumnIndex(KEY_YEAR)));
            movie.setFavorite(c.getInt(c.getColumnIndex(KEY_FAVORITE)));
            movie.setBookmark(c.getInt(c.getColumnIndex(KEY_BOOKMARK)));
            movies.add(movie);
        }
        c.close();
        return movies;
    }

    public Edition getCurrentEdition(Award award) {
        String sql = "SELECT *, MAX(" + KEY_NAME + ") FROM " + TABLE_EDITION +
                " WHERE " + KEY_ID_AWARD + " = " + award.getId();

        Cursor c = getReadableDatabase().rawQuery(sql, null);

        Edition edition = new Edition();

        while (c.moveToNext()) {
            edition.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            edition.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        }
        c.close();
        return edition;
    }

    public void updateMovie(Movie movie) {

        ContentValues dataMovie = new ContentValues();
        dataMovie.put(KEY_VIEWED, movie.getViewed());
        dataMovie.put(KEY_BOOKMARK, movie.getBookmark());
        dataMovie.put(KEY_FAVORITE, movie.getFavorite());

        String[] data = new String[]{movie.getId().toString()};

        getWritableDatabase().update(TABLE_MOVIE, dataMovie, KEY_ID + "=?", data);
    }
}