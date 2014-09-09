package mobi.nowtechnologies.server.persistence.domain.streamzine.badge;

import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(
        name = "sz_resolution",
        uniqueConstraints = @UniqueConstraint(name = "sz_resolution_dev_w_h", columnNames = {"device_type", "width", "height"}))
public class Resolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "device_type", nullable = false)
    private String deviceType;

    @Column(name = "width", nullable = false)
    private int width;

    @Column(name = "height", nullable = false)
    private int height;

    protected Resolution() {
    }

    public Resolution(int width, int height) {
        Assert.isTrue(width > 0);
        Assert.isTrue(height > 0);
        this.width = width;
        this.height = height;
    }

    public Resolution(String deviceType, int width, int height) {
        Assert.isTrue(width > 0);
        Assert.isTrue(height > 0);

        withDeviceType(deviceType);
        this.width = width;
        this.height = height;
    }

    public Resolution withDeviceType(String deviceType) {
        Assert.isTrue(DeviceType.all().contains(deviceType), "Passed: " + deviceType + ", allowed: " + DeviceType.all());
        this.deviceType = deviceType;
        return this;
    }

    public Resolution newResolution(int width, int height) {
        return new Resolution(deviceType, width, height);
    }

    public long getId() {
        return id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resolution that = (Resolution) o;

        if (height != that.height) return false;
        if (width != that.width) return false;
        if (!deviceType.equals(that.deviceType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = deviceType.hashCode();
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    public String getFullInfo() {
        return deviceType + "_" + getSizeInfo();
    }

    public String getSizeInfo() {
        return width + "x" + height;
    }

    @Override
    public String toString() {
        return getFullInfo();
    }
}
