package biz.netcentric.Slightly.renderer;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class PersonRendererTest {

    PersonRenderer renderer;

    @Before
    public void setUp() throws Exception {
        HttpServletRequest request =  mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter(anyString())).thenReturn("2");
        renderer = new PersonRenderer();
        renderer.getContext().getScope().put("request", renderer.getContext().getScope(), request);
    }


    @Test
    public void testParse() throws Exception {
        //MORE TESTS COMING
    }
}