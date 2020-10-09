package view;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import lattice.Position;
import lattice.Simulation;

import java.util.Optional;

/**
 * 3D visualisation of the pyrochlore lattice being simulated. Designed to allow the connections to be visually
 * verified.
 */
public class LatticeScene {
    private static final double X_SCALE = 20;
    private static final double Y_SCALE = Math.sqrt(3.0) * X_SCALE / 2.0;
    private static final double Z_SCALE = 6.0 * X_SCALE / Math.sqrt(12.0);
    private static final double maxX = X_SCALE * Position.SIZE;
    private static final double maxY = Y_SCALE * Position.SIZE;
    private static final double maxZ = Z_SCALE * Position.THICKNESS;

    private final Group root = new Group();
    private final PerspectiveCamera camera = new PerspectiveCamera();
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private final Translate zoom = new Translate(-maxX / 2, -maxY / 2, -100 - maxZ / 2);
    private final PhongMaterial evenParticleMaterial;
    private final PhongMaterial oddParticleMaterial;
    private final PhongMaterial linkMaterial;
    private Optional<DragStart> drag = Optional.empty();

    public LatticeScene(Simulation simulation) {
        this.evenParticleMaterial = new PhongMaterial(Color.RED);
        this.oddParticleMaterial = new PhongMaterial(Color.LIGHTPINK);
        this.linkMaterial = new PhongMaterial(Color.DARKGREY);
        Translate centre = new Translate(maxX / 2, maxY / 2, maxZ / 2);
        camera.getTransforms().addAll(centre, rotateX, rotateY, zoom);
        simulation.getMatrix().forEach(this::addParticle);
    }

    public Scene getScene() {
        Scene scene = new Scene(root, 500, 500, true);
        scene.setCamera(camera);
        scene.setOnMousePressed(me -> drag = Optional.of(new DragStart(
                new DragOrigin(rotateX.getAngle(), me.getX()),
                new DragOrigin(rotateY.getAngle(), me.getY()))));
        scene.setOnMouseDragged(me -> drag.ifPresent(start -> {
            rotateY.setAngle(start.y.angle + (me.getX() - start.x.coordinate) / 10.0);
            rotateX.setAngle(start.x.angle - (me.getY() - start.y.coordinate) / 10.0);
        }));
        scene.setOnMouseReleased(me -> drag = Optional.empty());
        scene.setOnScroll(se -> zoom.setZ(zoom.getZ() - se.getDeltaY() / 5));
        return scene;
    }

    private void addParticle(Position position, int charge) {
        Sphere sphere = new Sphere(X_SCALE / 3, 64);
        sphere.setMouseTransparent(true);
        sphere.setMaterial(position.z() % 2 == 0 ? evenParticleMaterial : oddParticleMaterial);
        Point3D point = positionToPoint(position);
        sphere.setTranslateX(point.getX());
        sphere.setTranslateY(point.getY());
        sphere.setTranslateZ(point.getZ());
        root.getChildren().add(sphere);
        position.neighbours().forEach(n -> addLink(position, n));
    }

    private void addLink(Position from, Position to) {
        if (Math.abs(from.x() - to.x()) > 2 || Math.abs(from.y() - to.y()) > 2 || Math.abs(from.z() - to.z()) > 2)
            return;
        Point3D fromPoint = positionToPoint(from);
        Point3D toPoint = positionToPoint(to);
        Point3D difference = toPoint.subtract(fromPoint);

        Point3D midPoint = fromPoint.midpoint(toPoint);
        Translate moveToMidpoint = new Translate(midPoint.getX(), midPoint.getY(), midPoint.getZ());

        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D axisOfRotation = difference.crossProduct(yAxis);
        double angle = -Math.toDegrees(Math.acos(difference.normalize().dotProduct(yAxis)));
        Rotate rotateAroundMidpoint = new Rotate(angle, axisOfRotation);

        Cylinder link = new Cylinder(X_SCALE / 10, difference.magnitude(), 64);
        link.getTransforms().addAll(moveToMidpoint, rotateAroundMidpoint);
        link.setMouseTransparent(true);
        link.setMaterial(linkMaterial);
        root.getChildren().add(link);
    }

    private Point3D positionToPoint(Position position) {
        return new Point3D(position.x() * X_SCALE, position.y() * Y_SCALE, position.z() * Z_SCALE);
    }

    private record DragOrigin(double angle, double coordinate) {
    }

    private record DragStart(DragOrigin x, DragOrigin y) {
    }
}
