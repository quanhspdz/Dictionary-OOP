import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import static Constant.Constant.apiKey;

public class Main {
    public static void main(String[] args) {
        Translate translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();
        Translation translation =
                translate.translate(
                        "Hello, how are you?",
                        Translate.TranslateOption.sourceLanguage("en"),
                        Translate.TranslateOption.targetLanguage("vi"),
                        // Use "base" for standard edition, "nmt" for the premium model.
                        Translate.TranslateOption.model("nmt"));

        System.out.printf("TranslatedText:\nText: %s\n", translation.getTranslatedText());
    }
}
