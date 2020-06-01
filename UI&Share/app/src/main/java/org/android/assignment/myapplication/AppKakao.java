package org.android.assignment.myapplication;

import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class AppKakao extends Application {
    private static AppKakao instance;
    public static AppKakao getGlobalApplicationContext(){
        if (instance == null) {
            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        }
        return instance;
    }

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     *
     * <p>Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.</p>
     *
     * <p>If you override this method, be sure to call {@code super.onCreate()}.</p>
     *
     * <p class="note">Be aware that direct boot may also affect callback order on
     * Android {@link Build.VERSION_CODES#N} and later devices.
     * Until the user unlocks the device, only direct boot aware components are
     * allowed to run. You should consider that all direct boot unaware
     * components, including such {@link ContentProvider}, are
     * disabled until user unlock happens, especially when component callback
     * order matters.</p>
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //Kakao Sdk 초기화
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    /**
     * This method is for use in emulated process environments.  It will
     * never be called on a production Android device, where processes are
     * removed by simply killing them; no user code (including this callback)
     * is executed when doing so.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
    public class KakaoSDKAdapter extends KakaoAdapter {

        /**
         * Session 설정에 필요한 option 값들을 받는다.
         *
         * @return default값들로 설정된 ISessionConfig
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig(){
                /**
                 * 로그인시 인증받을 타입을 지정한다. 지정하지 않을 시 가능한 모든 옵션이 지정된다. 예시) AuthType.KAKAO_TALK
                 *
                 * @return {@link AuthType} Kakao SDK 로그인을 하는 방식
                 */
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                    //모든 로그인 방식 사용 -> loginall
                }

                /**
                 * SDK 로그인시 사용되는 WebView에서 pause와 resume시에 Timer를 설정하여 CPU소모를 절약한다.
                 * true 를 리턴할경우 webview로그인을 사용하는 화면서 모든 webview에 onPause와 onResume 시에 Timer를 설정해 주어야 한다.
                 *
                 * @return true is set timer, false otherwise. default false.
                 */
                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                /**
                 * 로그인시 access token과 refresh token을 저장할 때 암호화 여부를 결정한다.
                 *
                 * @return true if using secure mode, false otherwise. default false.
                 */
                @Override
                public boolean isSecureMode() {
                    return false;
                }

                /**
                 * 일반 사용자가 아닌 Kakao와 제휴된 앱에서 사용되는 값으로, 값을 채워주지 않을경우 ApprovalType.INDIVIDUAL 값을 사용하게 된다.
                 *
                 * @return 설정한 ApprovalType. default ApprovalType.INDIVIDUAL
                 */
                @Nullable
                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                /**
                 * Kakao SDK 에서 사용되는 WebView 에서 email 입력폼에서 data 를 save 할지 여부를 결정한다.
                 * true 일 경우 SQLite의 접근이 제한되는 경우가 있음.
                 *
                 * @return SDK 에서 사용되는 WebView 에서 email 입력폼에서 data 를 save 할지 여부. default true.
                 * @deprecated since Android O. For more information, see <a href="https://developer.android.com/about/versions/o/android-8.0-changes.html">Android 8.0 Behavior changes</a>
                 */
                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        /**
         * KakaoSDK에서 Application에 필요한 정보를 받는 용도로 사용된다.
         *
         * @return Application context 정보를 가져올 수 있는 IApplicationConfig 객체
         */
        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return AppKakao.getGlobalApplicationContext();
                }
            };
        }
    }
}
