package gov.dot.fhwa.saxton.crossingrequest.geometry;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for convex polgyon region
 *
 * Ensures that the point inside region computation is done correctly for a variety of cases
 */
public class ConvexPolygonRegionTest {

    List<Location> squareVertices;
    List<Location> triangleVertices;

    @Before
    public void setUp() throws Exception {
        squareVertices = new ArrayList<>();
        squareVertices.add(new Location(0, 0));
        squareVertices.add(new Location(0, 1));
        squareVertices.add(new Location(1, 1));
        squareVertices.add(new Location(1, 0));

        triangleVertices = new ArrayList<>();
        triangleVertices.add(new Location(0, 0));
        triangleVertices.add(new Location(0.5, 1.0));
        triangleVertices.add(new Location(1, 0));
    }

    @Test
    public void testSquareContainsLocation() throws Exception {
        ConvexPolygonRegion square = new ConvexPolygonRegion(squareVertices);
        assertTrue(square.containsLocation(new Location(0.5, 0.5)));
    }

    @Test
    public void testSquareEdge() throws Exception {
        ConvexPolygonRegion square = new ConvexPolygonRegion(squareVertices);
        assertTrue(square.containsLocation(new Location(0.5, 0.0)));
    }

    @Test
    public void testSquareCorner() throws Exception {
        ConvexPolygonRegion square = new ConvexPolygonRegion(squareVertices);
        assertTrue(square.containsLocation(new Location(0.0, 0.0)));
    }

    @Test
    public void testSquareDoesNotContainLocation() throws Exception {
        ConvexPolygonRegion square = new ConvexPolygonRegion(squareVertices);
        assertFalse(square.containsLocation(new Location(0.5, 2.5)));
    }

    @Test
    public void testTriangleContainsLocation() throws Exception {
        ConvexPolygonRegion triangle = new ConvexPolygonRegion(triangleVertices);
        assertTrue(triangle.containsLocation(new Location(0.5, 0.5)));
    }

    @Test
    public void testTriangleDoesNotContainLocation() throws Exception {
        ConvexPolygonRegion triangle = new ConvexPolygonRegion(triangleVertices);
        assertFalse(triangle.containsLocation(new Location(10.0, 0.5)));
    }

    @Test
    public void testTriangleEdge() throws Exception {
        ConvexPolygonRegion triangle = new ConvexPolygonRegion(triangleVertices);
        assertTrue(triangle.containsLocation(new Location(0.5, 0.0)));
    }

    @Test
    public void testTriangleCorner() throws Exception {
        ConvexPolygonRegion triangle = new ConvexPolygonRegion(triangleVertices);
        assertTrue(triangle.containsLocation(new Location(0.0, 0.0)));
    }
}