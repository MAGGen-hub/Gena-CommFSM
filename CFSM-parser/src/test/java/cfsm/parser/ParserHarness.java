package cfsm.parser;

import io.vertx.core.Vertx;
import org.junit.After;
import org.junit.Before;

import java.util.Objects;

public class ParserHarness {

    protected Vertx vertx;
    protected Parser parser;

    @Before
    public void before() {
        vertx = Vertx.vertx();
        parser = new Parser(vertx);
    }

    @After
    public void after() {
        vertx.close();
    }

    protected String path(String path) {
        return Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)).getPath();
    }
}
