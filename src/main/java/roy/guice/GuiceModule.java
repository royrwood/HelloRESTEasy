package roy.guice;

import com.google.inject.Binder;
import com.google.inject.Module;

import roy.endpoints.HelloEndpoints;

public class GuiceModule implements Module {
    public void configure(final Binder binder) {
        binder.bind(HelloEndpoints.class);
    }
}
