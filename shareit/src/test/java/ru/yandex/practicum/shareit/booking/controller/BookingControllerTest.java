package ru.yandex.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.yandex.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.yandex.practicum.shareit.booking.exception.BookingExceptionHandler;
import ru.yandex.practicum.shareit.booking.model.Booking;
import ru.yandex.practicum.shareit.booking.model.BookingStatus;
import ru.yandex.practicum.shareit.booking.model.State;
import ru.yandex.practicum.shareit.booking.service.BookingService;
import ru.yandex.practicum.shareit.item.dto.ItemDto;
import ru.yandex.practicum.shareit.item.model.Item;
import ru.yandex.practicum.shareit.user.dto.UserDto;
import ru.yandex.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResponse;
    private Booking booking;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        // Добавляем обработчик исключений в MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new BookingExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Создаем тестовые данные
        user = new User(1L, "Test User", "user@test.com");
        item = new Item(1L, "Test Item", "Test Description", true, user, null, null);

        bookingDtoRequest = new BookingDtoRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true, null, null);
        UserDto userDto = new UserDto(1L, "Test User", "user@test.com");

        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.WAITING)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        when(bookingService.createBooking(any(BookingDtoRequest.class), anyLong()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(1L));
    }

    @Test
    void approveBooking_ShouldReturnApprovedBooking() throws Exception {
        Booking approvedBooking = Booking.builder()
                .id(1L)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserBookings_ShouldReturnListOfBookings() throws Exception {
        when(bookingService.getUserBookings(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOwnerBookings_ShouldReturnListOfBookings() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getUserBookings_ShouldReturnBadRequest_WhenInvalidState() throws Exception {
        mockMvc.perform(get("/bookings?state=INVALID_STATE")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }
}