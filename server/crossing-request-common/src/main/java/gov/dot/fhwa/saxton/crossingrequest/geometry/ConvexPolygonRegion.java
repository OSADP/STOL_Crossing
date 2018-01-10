package gov.dot.fhwa.saxton.crossingrequest.geometry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Region implementation using convex polygonal enclosure detection algorithm. Only works for Convex polygons but does
 * not check to ensure the data it is constructed with is convex, will simply provide inaccurate data. It is up to the
 * user of this class to ensure that the polygon data is convex.
 */
public class ConvexPolygonRegion implements Region {
    private List<Location> vertices;
    private double heading;
    private double headingEpsilon;

    public ConvexPolygonRegion() {
    }

    public ConvexPolygonRegion(List<Location> vertices) {
        this.vertices = Collections.unmodifiableList(vertices);
    }

    public ConvexPolygonRegion(Location... vertices) {
        this.vertices = Collections.unmodifiableList(Arrays.asList(vertices));
    }

    public void setVertices(List<Location> vertices) {
        this.vertices = Collections.unmodifiableList(vertices);
    }

    public List<Location> getVertices() {
        return vertices;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    /**
     * Get the acceptable heading error for this region. Headings may be within heading +/- headingEpsilon
     */
    public double getHeadingEpsilon() {
        return headingEpsilon;
    }

    /**
     * Set the acceptable heading error for this region. Headings may be within heading +/- headingEpsilon
     */
    public void setHeadingEpsilon(double headingEpsilon) {
        this.headingEpsilon = headingEpsilon;
    }

    /**
     * Use a raycasting algorithm to determine how many edges of the polygon are crossed by an infinite
     * line extended from our point.
     * <p>
     * Implementation of W. Randolph Franklin's algorithm published at:
     * https://wrf.ecse.rpi.edu//Research/Short_Notes/pnpoly.html
     */
    @Override
    public boolean containsLocation(Location loc) {
        int i =0 , j = 0;
        boolean inside = false;
        int nvert = vertices.size();
        for (i = 0, j = nvert -1; i < nvert; j = i++) {
            Location vtx1 = vertices.get(i);
            Location vtx2 = vertices.get(j);

            if ( ((vtx1.getLongitude() > loc.getLongitude()) != (vtx2.getLongitude() > loc.getLongitude())) &&
                    (loc.getLatitude() < (vtx2.getLatitude() - vtx1.getLatitude())
                            * (loc.getLongitude() - vtx1.getLongitude())
                            / (vtx2.getLongitude()-vtx1.getLongitude())
                            + vtx1.getLatitude())) {
                    inside = !inside;
            }
        }

        return inside;
    }

    @Override
    public boolean testHeading(double heading) {
        boolean correctlyOriented = true;
        if (this.heading + headingEpsilon > 360.0) {
            correctlyOriented = correctlyOriented &&
                    ((heading >= this.heading && heading <=360)
                    || ((heading > 0 && heading <= this.heading + headingEpsilon)));
        } else if (this.heading - headingEpsilon < 0) {
            correctlyOriented = correctlyOriented &&
                ((heading <= this.heading && heading >= 0)
                            || ((heading < 0 && heading >= this.heading + headingEpsilon)));
        } else {
            correctlyOriented = (heading >= (this.heading - headingEpsilon)) && (heading <= (this.heading + headingEpsilon));
        }

        return correctlyOriented;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConvexPolygonRegion that = (ConvexPolygonRegion) o;

        if (Double.compare(that.heading, heading) != 0) return false;
        if (Double.compare(that.headingEpsilon, headingEpsilon) != 0) return false;
        return !(vertices != null ? !vertices.equals(that.vertices) : that.vertices != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = vertices != null ? vertices.hashCode() : 0;
        temp = Double.doubleToLongBits(heading);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(headingEpsilon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ConvexPolygonRegion{" +
                "vertices=" + vertices +
                ", heading=" + heading +
                ", headingEpsilon=" + headingEpsilon +
                '}';
    }
}
