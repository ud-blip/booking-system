CREATE TABLE users (
   id BIGSERIAL PRIMARY KEY,
   username VARCHAR(50) UNIQUE NOT NULL,
   password VARCHAR(255) NOT NULL,
   email VARCHAR(100) UNIQUE NOT NULL,
   role VARCHAR(20) NOT NULL,
   version BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE resources (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   description TEXT,
   capacity INTEGER NOT NULL,
   location VARCHAR(100),
   working_hours_start TIME NOT NULL,
   working_hours_end TIME NOT NULL,
   cost_per_hour DOUBLE PRECISION,
   version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE bookings (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  resource_id BIGINT NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  purpose VARCHAR(255),
  status VARCHAR(20) NOT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (resource_id) REFERENCES resources(id)
);

CREATE TABLE booking_participants (
  booking_id BIGINT NOT NULL,
  participant VARCHAR(255),
  FOREIGN KEY (booking_id) REFERENCES bookings(id)
);


CREATE INDEX idx_bookings_resource_time ON bookings(resource_id, start_time, end_time);
