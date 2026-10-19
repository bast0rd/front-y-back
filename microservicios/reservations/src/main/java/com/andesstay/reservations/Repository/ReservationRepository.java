package com.andesstay.reservations.Repository;

import com.andesstay.reservations.Model.Reservation;
import com.andesstay.reservations.Model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByGuestId(String guestId);

    List<Reservation> findByStartDateBetween(LocalDateTime from, LocalDateTime to);

    List<Reservation> findByStatusAndStartDateBetween(ReservationStatus status, LocalDateTime from, LocalDateTime to);
}
