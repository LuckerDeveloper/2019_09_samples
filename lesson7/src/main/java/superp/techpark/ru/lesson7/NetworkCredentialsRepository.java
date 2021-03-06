package superp.techpark.ru.lesson7;

import java.util.HashMap;
import java.util.Map;

import superp.techpark.ru.lesson7.executors.AppExecutors;


public class NetworkCredentialsRepository
        implements CredentialsRepository{

    Map<String, String> mCredentials = new HashMap<String,String>() {{
        put("test", "test");
        put("pupkin", "qa");
    }};

    @Override
    public void validateCredentials(final String login, final String pass,
                                    final ValidationCallback validationCallback) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                // TODO do network validation
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mCredentials.containsKey(login) &&
                        mCredentials.get(login).equals(pass)) {
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            validationCallback.onSuccess();
                        }
                    });
                } else {
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            validationCallback.onError();
                        }
                    });
                }
            }
        });
    }
}
