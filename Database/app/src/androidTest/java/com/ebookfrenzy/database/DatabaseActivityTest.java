package com.ebookfrenzy.database;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.android.libraries.cloudtesting.screenshots.ScreenShotter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DatabaseActivityTest {

    @Rule
    public ActivityTestRule<DatabaseActivity> mActivityTestRule = new ActivityTestRule<>(DatabaseActivity.class);

    @Test
    public void databaseActivityTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.productName), isDisplayed()));
        appCompatEditText.perform(replaceText("Firebase Books"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.productQuantity), isDisplayed()));
        appCompatEditText2.perform(replaceText("10"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.button3), withText("Add"), isDisplayed()));
        appCompatButton.perform(click());

        ScreenShotter.takeScreenshot("after_add", mActivityTestRule.getActivity());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.productName), isDisplayed()));
        appCompatEditText3.perform(replaceText("Firebase Books"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.button2), withText("Find"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.productQuantity), withText("10"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.TableLayout.class),
                                        2),
                                1),
                        isDisplayed()));
        editText.check(matches(withText("10")));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.button), withText("Delete"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.productName), isDisplayed()));
        appCompatEditText4.perform(replaceText("Firebase Book"), closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.button2), withText("Find"), isDisplayed()));
        appCompatButton4.perform(click());
        ScreenShotter.takeScreenshot("after_find", mActivityTestRule.getActivity());

        ViewInteraction textView = onView(
                allOf(withId(R.id.productID), withText("No Match Found"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.TableLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("No Match Found")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
