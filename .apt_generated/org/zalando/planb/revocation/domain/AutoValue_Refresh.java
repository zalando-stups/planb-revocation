
package org.zalando.planb.revocation.domain;

import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Refresh extends Refresh {

  private final Integer refreshFrom;
  private final Integer refreshTimestamp;

  AutoValue_Refresh(
      Integer refreshFrom,
      Integer refreshTimestamp) {
    if (refreshFrom == null) {
      throw new NullPointerException("Null refreshFrom");
    }
    this.refreshFrom = refreshFrom;
    if (refreshTimestamp == null) {
      throw new NullPointerException("Null refreshTimestamp");
    }
    this.refreshTimestamp = refreshTimestamp;
  }

  @com.fasterxml.jackson.annotation.JsonProperty(value = "refresh_from")
  @Override
  public Integer refreshFrom() {
    return refreshFrom;
  }

  @com.fasterxml.jackson.annotation.JsonProperty(value = "refresh_timestamp")
  @Override
  public Integer refreshTimestamp() {
    return refreshTimestamp;
  }

  @Override
  public String toString() {
    return "Refresh{"
        + "refreshFrom=" + refreshFrom + ", "
        + "refreshTimestamp=" + refreshTimestamp
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Refresh) {
      Refresh that = (Refresh) o;
      return (this.refreshFrom.equals(that.refreshFrom()))
           && (this.refreshTimestamp.equals(that.refreshTimestamp()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= refreshFrom.hashCode();
    h *= 1000003;
    h ^= refreshTimestamp.hashCode();
    return h;
  }

}
