/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.providers.contacts;

import android.provider.ContactsContract.FullNameStyle;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@SmallTest
public class ContactLocaleUtilsTest extends AndroidTestCase {
    private static final String TAG = "ContactLocaleUtilsTest";

    private static final String PHONE_NUMBER_1 = "+1 (650) 555-1212";
    private static final String PHONE_NUMBER_2 = "650-555-1212";
    private static final String LATIN_NAME = "John Smith";
    private static final String LATIN_NAME_2 = "John Paul Jones";
    private static final String KANJI_NAME = "\u65e5";
    private static final String ARABIC_NAME = "\u0646\u0648\u0631"; /* Noor */
    private static final String CHINESE_NAME = "\u675C\u9D51";
    private static final String SERBIAN_NAME = "\u0408\u0435\u043B\u0435\u043D\u0430";
    private static final String UKRAINIAN_NAME = "\u0406";
    private static final String UKRAINIAN_NAME_2 = "\u0407";
    private static final String UKRAINIAN_NAME_3 = "\u0490";
    private static final String CHINESE_LATIN_MIX_NAME_1 = "D\u675C\u9D51";
    private static final String CHINESE_LATIN_MIX_NAME_2 = "MARY \u675C\u9D51";
    private static final String[] CHINESE_NAME_KEY = {"\u9D51", "\u675C\u9D51", "JUAN", "DUJUAN",
            "J", "DJ"};
    private static final String[] CHINESE_LATIN_MIX_NAME_1_KEY = {"\u9D51", "\u675C\u9D51",
        "D \u675C\u9D51", "JUAN", "DUJUAN", "J", "DJ", "D DUJUAN", "DDJ"};
    private static final String[] CHINESE_LATIN_MIX_NAME_2_KEY = {"\u9D51", "\u675C\u9D51",
        "MARY \u675C\u9D51", "JUAN", "DUJUAN", "MARY DUJUAN", "J", "DJ", "MDJ"};
    private static final String[] LATIN_NAME_KEY = {"John Smith", "Smith", "JS", "S"};
    private static final String[] LATIN_NAME_KEY_2 = {
        "John Paul Jones", "Paul Jones", "Jones", "JPJ", "PJ", "J"};
    private static final String[] LABELS_EN_US = {
        "\u2026", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "#", ""};
    private static final String[] LABELS_JA_JP = {
        "…", "\u3042", "\u304B", "\u3055", "\u305F", "\u306A", "\u306F",
        "\u307E", "\u3084", "\u3089", "\u308F", "\u4ED6",
        "…", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "#", ""};
    private static final String[] LABELS_ZH_TW = {
        "…", "1\u5283", "2\u5283", "3\u5283", "4\u5283", "5\u5283", "6\u5283",
        "7\u5283", "8\u5283", "9\u5283", "10\u5283", "11\u5283", "12\u5283",
        "13\u5283", "14\u5283", "15\u5283", "16\u5283", "17\u5283", "18\u5283",
        "19\u5283", "20\u5283", "21\u5283", "22\u5283", "23\u5283", "24\u5283",
        "25\u5283", "26\u5283", "27\u5283", "28\u5283", "29\u5283", "30\u5283",
        "31\u5283", "32\u5283", "33\u5283",
        "35\u5283", "36\u5283", "39\u5283", "48\u5283",
        "…", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "#", ""};
    private static final String[] LABELS_KO = {
        "…", "\u3131", "\u3134", "\u3137", "\u3139", "\u3141", "\u3142",
        "\u3145", "\u3147", "\u3148", "\u314A", "\u314B", "\u314C", "\u314D",
        "\u314E", "…",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "#", ""};
    private static final String[] LABELS_AR = {
        "…",
        "\u0621", "\u0628", "\u067E", "\u062A", "\u062B", "\u0679", "\u062C",
        "\u0686", "\u062D", "\u062E", "\u062F", "\u0630", "\u0688", "\u0631",
        "\u0632", "\u0691", "\u0698", "\u0633", "\u0634", "\u0635", "\u0636",
        "\u0637", "\u0638", "\u0639", "\u063A", "\u0641", "\u0642", "\u0643",
        "\u06A9", "\u06AF", "\u0644", "\u0645", "\u0646", "\u0647", "\u06BE",
        "\u06C1", "\u0648", "\u064A", "\u06D2",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "#", ""};

    private static final String JAPANESE_MISC = "\u4ed6";

    private static final Locale LOCALE_ARABIC = new Locale("ar");
    private static final Locale LOCALE_SERBIAN = new Locale("sr");
    private static final Locale LOCALE_UKRAINIAN = new Locale("uk");
    private static final Locale LOCALE_SPANISH = new Locale("es");
    private static final Locale LOCALE_GREEK = new Locale("el");

    private String getLabel(String name) {
        ContactLocaleUtils utils = ContactLocaleUtils.getInstance();
        int bucketIndex = utils.getBucketIndex(name);
        return utils.getBucketLabel(bucketIndex);
    }

    private Iterator<String> getNameLookupKeys(String name, int nameStyle) {
        ContactLocaleUtils utils = ContactLocaleUtils.getInstance();
        return utils.getNameLookupKeys(name, nameStyle);
    }

    private ArrayList<String> getLabels() {
        ContactLocaleUtils utils = ContactLocaleUtils.getInstance();
        return utils.getLabels();
    }

    public void testEnglishContactLocaleUtils() throws Exception {
        ContactLocaleUtils.setLocaleForTest(Locale.ENGLISH);
        assertEquals("#", getLabel(PHONE_NUMBER_1));
        assertEquals("#", getLabel(PHONE_NUMBER_2));
        assertEquals("J", getLabel(LATIN_NAME));
        assertEquals("\u2026", getLabel(CHINESE_NAME));
        assertEquals("D", getLabel(CHINESE_LATIN_MIX_NAME_1));
        assertEquals("B", getLabel("Bob Smith"));

        assertEquals("\u0646", getLabel(ARABIC_NAME));
        assertEquals("\u0408", getLabel(SERBIAN_NAME));
        // Updated in CLDR 27/ICU 55:
        // http://cldr.unicode.org/index/downloads/cldr-27#TOC-Changes-to-Collation
        assertEquals("\u0406", getLabel(UKRAINIAN_NAME));

        assertNull(getNameLookupKeys(LATIN_NAME, FullNameStyle.UNDEFINED));
        verifyLabels(getLabels(), LABELS_EN_US);
    }

    public void testJapaneseContactLocaleUtils() throws Exception {
        ContactLocaleUtils.setLocaleForTest(Locale.JAPAN);
        assertEquals("#", getLabel(PHONE_NUMBER_1));
        assertEquals("#", getLabel(PHONE_NUMBER_2));
        assertEquals(JAPANESE_MISC, getLabel(KANJI_NAME));
        assertEquals("J", getLabel(LATIN_NAME));
        assertEquals(JAPANESE_MISC, getLabel(CHINESE_NAME));
        assertEquals("D", getLabel(CHINESE_LATIN_MIX_NAME_1));

        assertNull(getNameLookupKeys(CHINESE_NAME, FullNameStyle.CJK));
        assertNull(getNameLookupKeys(CHINESE_NAME, FullNameStyle.CHINESE));

        assertEquals("B", getLabel("Bob Smith"));
        verifyLabels(getLabels(), LABELS_JA_JP);
    }

    public void testChineseContactLocaleUtils() throws Exception {
        ContactLocaleUtils.setLocaleForTest(Locale.SIMPLIFIED_CHINESE);
        assertEquals("#", getLabel(PHONE_NUMBER_1));
        assertEquals("#", getLabel(PHONE_NUMBER_2));
        assertEquals("J", getLabel(LATIN_NAME));
        assertEquals("D", getLabel(CHINESE_NAME));
        assertEquals("D", getLabel(CHINESE_LATIN_MIX_NAME_1));
        assertEquals("B", getLabel("Bob Smith"));
        verifyLabels(getLabels(), LABELS_EN_US);

        ContactLocaleUtils.setLocaleForTest(Locale.TRADITIONAL_CHINESE);
        assertEquals("#", getLabel(PHONE_NUMBER_1));
        assertEquals("#", getLabel(PHONE_NUMBER_2));
        assertEquals("J", getLabel(LATIN_NAME));
        assertEquals("7\u5283", getLabel(CHINESE_NAME));
        assertEquals("D", getLabel(CHINESE_LATIN_MIX_NAME_1));

        ContactLocaleUtils.setLocaleForTest(Locale.SIMPLIFIED_CHINESE);
        Iterator<String> keys = getNameLookupKeys(CHINESE_NAME,
                FullNameStyle.CHINESE);
        verifyKeys(keys, CHINESE_NAME_KEY);

        keys = getNameLookupKeys(CHINESE_LATIN_MIX_NAME_1, FullNameStyle.CHINESE);
        verifyKeys(keys, CHINESE_LATIN_MIX_NAME_1_KEY);

        keys = getNameLookupKeys(CHINESE_LATIN_MIX_NAME_2, FullNameStyle.CHINESE);
        verifyKeys(keys, CHINESE_LATIN_MIX_NAME_2_KEY);

        ContactLocaleUtils.setLocaleForTest(Locale.TRADITIONAL_CHINESE);
        assertEquals("B", getLabel("Bob Smith"));
        verifyLabels(getLabels(), LABELS_ZH_TW);
    }

    public void testPinyinEnabledSecondaryLocale() throws Exception {
        ContactLocaleUtils.setLocales(
                LocaleSet.newForTest(Locale.ENGLISH, Locale.SIMPLIFIED_CHINESE));
        assertEquals("D", getLabel(CHINESE_NAME));

        Iterator<String> keys = getNameLookupKeys(CHINESE_NAME,
                FullNameStyle.CHINESE);
        verifyKeys(keys, CHINESE_NAME_KEY);
    }

    public void testPinyinDisabledSecondaryLocale() throws Exception {
        ContactLocaleUtils.setLocales(
                LocaleSet.newForTest(Locale.ENGLISH, Locale.JAPAN));
        assertEquals(JAPANESE_MISC, getLabel(CHINESE_NAME));

        assertNull(getNameLookupKeys(CHINESE_NAME, FullNameStyle.CHINESE));
        assertNull(getNameLookupKeys(CHINESE_NAME, FullNameStyle.CJK));
    }

    public void testChineseStyleNameWithDifferentLocale() throws Exception {
        ContactLocaleUtils.setLocaleForTest(Locale.ENGLISH);
        assertNull(getNameLookupKeys(CHINESE_NAME, FullNameStyle.CHINESE));
        assertNull(getNameLookupKeys(CHINESE_NAME, FullNameStyle.CJK));

        ContactLocaleUtils.setLocaleForTest(Locale.SIMPLIFIED_CHINESE);
        Iterator<String> keys = getNameLookupKeys(CHINESE_NAME,
                FullNameStyle.CJK);
        verifyKeys(keys, CHINESE_NAME_KEY);
        keys = getNameLookupKeys(LATIN_NAME, FullNameStyle.WESTERN);
        verifyKeys(keys, LATIN_NAME_KEY);
        keys = getNameLookupKeys(LATIN_NAME_2, FullNameStyle.WESTERN);
        verifyKeys(keys, LATIN_NAME_KEY_2);

        ContactLocaleUtils.setLocaleForTest(Locale.TRADITIONAL_CHINESE);
        assertNull(getNameLookupKeys(CHINESE_NAME, FullNameStyle.CJK));
    }

    public void testKoreanContactLocaleUtils() throws Exception {
        ContactLocaleUtils.setLocaleForTest(Locale.KOREA);
        assertEquals("\u3131", getLabel("\u1100"));
        assertEquals("\u3131", getLabel("\u3131"));
        assertEquals("\u3131", getLabel("\u1101"));
        assertEquals("\u314e", getLabel("\u1161"));
        assertEquals("B", getLabel("Bob Smith"));
        verifyLabels(getLabels(), LABELS_KO);
    }

    public void testArabicContactLocaleUtils() throws Exception {
        ContactLocaleUtils.setLocaleForTest(LOCALE_ARABIC);
        assertEquals("\u0646", getLabel(ARABIC_NAME));
        assertEquals("B", getLabel("Bob Smith"));
        verifyLabels(getLabels(), LABELS_AR);
    }

    public void testSerbianContactLocaleUtils() throws Exception {
        ContactLocaleUtils.setLocaleForTest(LOCALE_SERBIAN);
        assertEquals("\u0408", getLabel(SERBIAN_NAME));
        assertEquals("B", getLabel("Bob Smith"));
    }

    public void testUkrainianContactLocaleUtils() throws Exception {
        ContactLocaleUtils.setLocaleForTest(LOCALE_UKRAINIAN);
        assertEquals("\u0406", getLabel(UKRAINIAN_NAME));
        assertEquals("\u0407", getLabel(UKRAINIAN_NAME_2));
        assertEquals("\u0490", getLabel(UKRAINIAN_NAME_3));
        assertEquals("B", getLabel("Bob Smith"));
    }

    public void testGermanContactLocaleUtils() throws Exception {
        ContactLocaleUtils.setLocaleForTest(Locale.GERMANY);
        assertEquals("S", getLabel("Sacher"));

        // ICU 51 has labels Sch and St. These were removed in ICU 52
        assertEquals("S", getLabel("Schiller"));
        assertEquals("S", getLabel("Steiff"));
        verifyLabels(getLabels(), LABELS_EN_US);
    }

    public void testOtherLocales() throws Exception {
        ContactLocaleUtils.setLocaleForTest(Locale.ENGLISH);

        assertEquals("N", getLabel("n"));
        assertEquals("N", getLabel("ñ"));

        ContactLocaleUtils.setLocaleForTest(LOCALE_SPANISH);

        assertEquals("N", getLabel("n"));
        assertEquals("Ñ", getLabel("ñ"));

        ContactLocaleUtils.setLocaleForTest(Locale.ENGLISH, LOCALE_SPANISH);

        assertEquals("N", getLabel("n"));
        assertEquals("N", getLabel("ñ")); // TODO This should ideally return Ñ.

    }

    private void verifyKeys(final Iterator<String> resultKeys, final String[] expectedKeys)
            throws Exception {
        HashSet<String> allKeys = new HashSet<String>();
        while (resultKeys.hasNext()) {
            allKeys.add(resultKeys.next());
        }
        assertEquals(new HashSet<String>(Arrays.asList(expectedKeys)), allKeys);
    }

    // Verify that the initial set of resultLabels matches the expectedLabels.
    // Ignore the (large) number of secondary locale labels that make up the
    // tail labels in the result set right before the final "#" and "" buckets.
    private void verifyLabels(final ArrayList<String> resultLabels,
            final String[] expectedLabels) throws Exception {
        final List<String> expectedLabelList = Arrays.asList(expectedLabels);
        final int numLabels = expectedLabelList.size() - 2;
        assertEquals(expectedLabelList.subList(0, numLabels),
                resultLabels.subList(0, numLabels));
    }
}
