package com.developerphil.adbidea.compatibility;

import com.developerphil.adbidea.test.Holder;
import org.joor.ReflectException;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class BackwardCompatibleGetterTest {


    @Test
    public void onlyCallCurrentImplementationWhenItIsValid() throws Exception {
        final Holder<Boolean> currentImplementation = new Holder<Boolean>(false);

        new BackwardCompatibleGetter<Boolean>() {
            @Override
            protected Boolean getCurrentImplementation() throws Throwable {
                currentImplementation.set(true);
                return true;
            }

            @Override
            protected Boolean getPreviousImplementation() {
                fail("should not be called");
                return true;
            }
        }.get();

        assertThat(currentImplementation.get(), is(true));

    }

    @Test
    public void callPreviousImplementationWhenCurrentThrowsErrors() throws Exception {
        expectPreviousImplementationIsCalledFor(new ClassNotFoundException());
        expectPreviousImplementationIsCalledFor(new NoSuchMethodException());
        expectPreviousImplementationIsCalledFor(new NoSuchFieldException());
        expectPreviousImplementationIsCalledFor(new LinkageError());
        expectPreviousImplementationIsCalledFor(new ReflectException());
    }


    @Test(expected = RuntimeException.class)
    public void throwExceptionsWhenTheyAreNotRelatedToBackwardCompatibility() throws Exception {
        new BackwardCompatibleGetter<Boolean>() {
            @Override
            protected Boolean getCurrentImplementation() throws Throwable {
                throw new RuntimeException("exception!");
            }

            @Override
            protected Boolean getPreviousImplementation() {
                fail("should not be called");
                return true;
            }
        }.get();
    }


    private static void expectPreviousImplementationIsCalledFor(final Throwable throwable) throws Exception {
        final Holder<Boolean> calledPreviousImplementation = new Holder<Boolean>();
        new BackwardCompatibleGetter<Boolean>() {
            @Override
            protected Boolean getCurrentImplementation() throws Throwable {
                throw throwable;
            }

            @Override
            protected Boolean getPreviousImplementation() {
                calledPreviousImplementation.set(true);
                return null;
            }
        }.get();

        assertThat(calledPreviousImplementation.get(), is(true));
    }

}