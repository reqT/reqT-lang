package reqt
package examples

/** Examples from "Software Requirements - Styles and techniques" by S. Lauesen (2002)". */
object Lauesen:
  val allExamples = 
    Seq(GoalDesignScale, WhySpecExample, ContextDiagramSimple, ContextDiagramInterfaces, DataRelations, DataEntities)

  val GoalDesignScale = Model(
    Title("Goal-Design-scale"),
    Goal("accuracy").has(
      Spec("Our pre-calculations shall hit within 5%"),
    ),
    Feature("quotation").has(
      Spec("Product shall support cost recording and quotation with experience data"),
    ),
    Function("experienceData").has(
      Spec("Product shall have recording and retrieval functions for experience data"),
    ),
    Design("screenX").has(
      Spec("System shall have screen pictures as shown in Fig. X"),
    ),
  )

  val WhySpecExample = Model(
    Title("Why-Spec-Example"),
    Feature("navigate").has(
      Why("Measuring neural response is a bit painful to the  patient. Electrodes must be kept in place ... So both hands should be at the patient during a measurement."),
      Spec("It shall be possible to perform the commands start, stop, ... with both hands at the patient."),
      Example("Might be done with mini keyboard (wrist keys), foot pedal, voice recognition, etc."),
    ),
  )

  val ContextDiagramSimple = Model(
    Title("Context Diagram, simple"),
    Section("context").has(
      Product("hotelApp").interacts(
        User("receptionist"),
        User("guest"),
        System("accounting"),
        System("telephony"),
      ),
    ),
  )

  val ContextDiagramInterfaces = Model(
    Title("Context Diagram with Interfaces"),
    Product("hotelApp").has(
      Interface("receptionUI").has(
        User("Receptionist"),
      ),
      Interface("guestUI").has(
        User("Guest"),
      ),
      Interface("phoneAPI").has(
        System("Telephony"),
      ),
      Interface("accountAPI").has(
        System("Accounting"),
      ),
    ),
    Data("InterfaceIO").has(
      Interface("receptionUI").has(
        Input("booking"),
        Input("checkOut"),
        Output("serviceNote"),
      ),
      Interface("guestUI").has(
        Output("confirmation"),
        Output("invoice"),
      ),
    ),
  )

  val DataRelations = Model(
      Title("Data Relations"),
      Class("Guest").relates(
        Class("Stay"),
        Min(1),
      ),
      Class("Stay").relates(
        Class("RoomState"),
        Class("Service"),
        Min(1),
      ),
      Class("ServiceType").relates(
        Class("Service"),
        Min(1),
      ),
      Class("Room").relates(
        Class("RoomState"),
        Min(1),
      ),
  )

  val DataEntities = Model(
    Title("Data Entities"),
    Class("Guest").has(
      Field("name"),
      Field("address1"),
      Field("address2"),
      Field("address3"),
      Field("passport"),
    ),
    Class("Stay").has(
      Field("stayId"),
      Field("paymethod"),
      Field("employee"),
    ),
    Class("ServiceType").has(
      Field("name"),
      Field("price"),
    ),
    Class("Service").has(
      Field("serviceDate"),
      Field("serviceCount"),
    ),
    Class("Room").has(
      Field("roomId"),
      Field("bedCount"),
      Field("roomType"),
      Field("price1"),
      Field("price2"),
    ),
    Class("RoomState").has(
      Field("date"),
      Field("personCount"),
      Field("state"),
    ),
  )

  val StateMachine = Model(
    Title("State Machine"),
    Section("roomState").has(
      Title("Room State Model"),
      State("free").has(
        Event("book").precedes(
          State("booked"),
        ),
        Event("checkin").precedes(
          State("occupied"),
        ),
        Event("changeRoom").precedes(
          State("occupied"),
        ),
        Event("repair").precedes(
          State("repairing"),
        ),
      ),
      State("booked").has(
        Event("checkIn").precedes(
          State("occupied"),
        ),
        Event("cancel").precedes(
          State("free"),
        ),
      ),
      State("occupied").has(
        Event("checkout").precedes(
          State("free"),
        ),
        Event("changeRoom").precedes(
          State("free"),
        ),
      ),
      State("repairing").has(
        Event("done").precedes(
          State("free"),
        ),
      ),
    ),
  )

  val QualityRequirements = Model(
    Section("quality").has(
      Text("This section contains system-wide quality requirements."),
      Quality("databaseCapacity").has(
        Spec("#guests < 10,000 growing 20% per year, #rooms < 1,000"),
      ),
      Quality("calendarAccuracy").has(
        Spec("Bookings shall be possible at least two years ahead."),
      ),
      Quality("forecastPerformance").has(
        Spec("Product shall compute a room occupation forecast within ___ minutes. (Customer expects one minute.)"),
      ),
      Quality("taskUsability").has(
        Spec("Novice users shall perform tasks Q and R in 15 minutes. Experienced users shall perform tasks Q, R, S in 2 minutes."),
      ),
      Quality("taskUsability").relates(
        Task("Q"),
        Task("R"),
        Task("S"),
      ),
      Quality("peakLoadPerformance").has(
        Spec("Product shall be able to process 100 payment transactions per second in peak load."),
      ),
    ),
  )

  val ModelWithSections = Model(
    Title("Test Model"),
    Text("This is a model to test html generation."),
    Feature("topStuff") has Spec("Hello top-level stuff."),
    Feature("deepTopStuff") has (Feature("Gurka") has Spec("hejsan")),
    Section("context") has (
      Text("This section describes the context of the system."),
      Image("context-diagram.svg"),
      Product("hotelApp") implements (
        Interface("receptionUI") has User("receptionist"),
        Interface("guestUI") has User("guest"),
        Interface("phoneAPI") requires System("telephony"),
        Interface("accountAPI") requires System("accounting")),
      Interface("receptionUI") has (
        Input("booking"), Input("checkOut"),
        Output("serviceNote")),
      Interface("guestUI") has (
        Output("confirmation"), Output("invoice"))
    )
  ) ++ QualityRequirements

  val TaskHotelReceptionWork = Model(
    Task("receptionWork") has (
      Task("booking"),
      Task("checkIn") has (
        Why("Guest wants room."),
        Frequency(3),
        Spec("Give guest a room, mark it as occupied and start account. Frequency scale is median number of check-ins/room/week. Trigger: A guest arrives. Critical: Group tour with 50 guests."),
        Task("findRoom"),
        Task("recordGuest") has
          Spec("variants: a) Guest has booked in advance, b) No suitable room"),
        Task("deliverKey"))))

object Prioritization:
  val Prio100DollarTest = Model(
    Stakeholder("a").has(
      Prio(2),
      Req("1") has Benefit(5),
      Req("2") has Benefit(300),
      Req("3") has Benefit(8),
      Req("4") has Benefit(9),
      Req("5") has Benefit(100),
      Req("6") has Benefit(10),
      Req("7") has Benefit(20)),
    Stakeholder("b").has(
      Prio(4),
      Req("1") has Benefit(100),
      Req("2") has Benefit(7),
      Req("3") has Benefit(20),
      Req("4") has Benefit(80),
      Req("5") has Benefit(10),
      Req("6") has Benefit(90),
      Req("7") has Benefit(20)))

  def NormalizedBenefits: Model =
    val m = Prio100DollarTest
    val shs = m.ents.filter(_.et == Stakeholder).distinct
    val rs = m.ents.filter(_.et == Req).distinct
    val prioSum = shs.flatMap(s => m/s.has/Prio).sum
    val benefitSum = shs.map: s => 
        s -> (m/s.has).intAttrs.collect{ case IntAttr(Benefit, b) => b}.sum
      .toMap
    val normalized = rs.map(r =>
      r has Benefit(
        math.round(shs.map(s =>
          (m/s.has/Prio).head*(m/s.has/r.has/Benefit).head * 100.0 / (benefitSum(s)*prioSum)).sum).toInt))
    Model(Title("Prioritization Normalized Benefits") +: normalized)

object QUPER:
  val StartupQuality = Model(
    Quality("mtts") has (
      Gist("Mean time to startup"),
      Spec("Measured in milliseconds using Test startup"),
      Breakpoint("Utility") has Value(4000),
      Breakpoint("Differentiation") has Value(1500),
      Breakpoint("Saturation") has Value(200),
      Target("basic") has (
          Value(2000),
          Comment("Probably possible with existing architecture.")),
      Target("strech") has (
          Value(1100),
          Comment("Probably needs new architecture.")),
      Barrier("first") has Value(2100),
      Barrier("second") has Value(1000),
      Product("competitorX") has Value(2000),
      Product("competitorY") has Value(3000)
    ),
    Test("startup") verifies Quality("mtts"),
    Test("startup") has (
      Spec("Calculate average time in milliseconds of the startup time over 10  executions from start button is pressed to logon screen is shown."),
      Target("stretch")
    )
  )

object VariabilityModeling:
  val ColorVariation = Model(
  Component("apperance") has (
    VariationPoint("color") has (
      Min(0), Max(2),
      Variant("blue"), Variant("red"), Variant("green")),
    VariationPoint("shape") has (
      Min(1), Max(1), Variant("round"), Variant("square")),
    VariationPoint("payment") has (
      Min(1), Max(2), Variant("cash"), Variant("credit")),
    VariationPoint("payment") requires Variant("cash"), /* mandatory */
    Variant("round") excludes Variant("red"),
    Variant("green") requires Variant("square")),
  Component("apperance") requires VariationPoint("shape"), /* mandatory */
  Product("free") requires Component("apperance"),
  Product("free") binds (
    VariationPoint("shape") binds Variant("round")),
  Product("premium") requires Component("apperance"),
  Product("premium") binds ( /* violating variability constraints */
    VariationPoint("color") binds (Variant("red"), Variant("green")),
    VariationPoint("shape") binds (Variant("round"), Variant("square")),
    VariationPoint("payment") binds Variant("cash")))



