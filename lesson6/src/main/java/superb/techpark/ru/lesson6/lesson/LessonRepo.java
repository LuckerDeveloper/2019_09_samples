package superb.techpark.ru.lesson6.lesson;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import superb.techpark.ru.lesson6.network.ApiRepo;
import superb.techpark.ru.lesson6.network.LessonApi;

public class LessonRepo {
    private static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
    private final static MutableLiveData<List<Lesson>> mLessons = new MutableLiveData<>();

    static {
        mLessons.setValue(Collections.<Lesson>emptyList());
    }

    private final Context mContext;

    public LessonRepo(Context context) {
        mContext = context;
    }

    public LiveData<List<Lesson>> getLessons() {
        return mLessons;
    }

    public void refresh() {
        LessonApi api = ApiRepo.from(mContext).getLessonApi();
        api.getAll().enqueue(new Callback<List<LessonApi.LessonPlain>>() {
            @Override
            public void onResponse(Call<List<LessonApi.LessonPlain>> call,
                                   Response<List<LessonApi.LessonPlain>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mLessons.postValue(transform(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<LessonApi.LessonPlain>> call, Throwable t) {
                Log.e("LessonRepo", "Failed to load", t);
            }
        });
    }

    private static List<Lesson> transform(List<LessonApi.LessonPlain> plains) {
        List<Lesson> result = new ArrayList<>();
        for (LessonApi.LessonPlain lessonPlain : plains) {
            try {
                Lesson lesson = map(lessonPlain);
                result.add(lesson);
                Log.e("LessonRepo", "Loaded " + lesson.getName() + " #" + lesson.getId());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static Lesson map(LessonApi.LessonPlain lessonPlain) throws ParseException {
        return new Lesson(
                lessonPlain.id,
                lessonPlain.name,
                sSimpleDateFormat.parse(lessonPlain.date),
                lessonPlain.place,
                lessonPlain.is_rk,
                lessonPlain.rating
        );
    }
}
