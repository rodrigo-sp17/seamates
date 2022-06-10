import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:i18n_extension/i18n_widget.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/data/shift.dart';
import 'package:sea_mates/model/shift_list_model.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/view/day_view.dart';
import 'package:sea_mates/view/shift_add_view.dart';
import 'package:table_calendar/table_calendar.dart';

class CalendarView extends StatefulWidget {
  const CalendarView(this.defaultActions);
  final List<Widget> defaultActions;

  @override
  State<StatefulWidget> createState() => _CalendarState();
}

class _CalendarState extends State<CalendarView> {
  final unavailabilityStartDates =
      new LinkedHashSet<DateTime>(equals: isSameDay, hashCode: _getHashCode);
  final unavailabilityEndDates =
      new LinkedHashSet<DateTime>(equals: isSameDay, hashCode: _getHashCode);
  final boardingDates =
      new LinkedHashSet<DateTime>(equals: isSameDay, hashCode: _getHashCode);
  final leavingDates =
      new LinkedHashSet<DateTime>(equals: isSameDay, hashCode: _getHashCode);
  final unavailableDates =
      new LinkedHashSet<DateTime>(equals: isSameDay, hashCode: _getHashCode);

  final unStartColor = Colors.amber;
  final boardingColor = Colors.red;
  final unavailableColor = Colors.grey;
  final leavingColor = Colors.lightGreen;
  final unEndColor = Colors.cyan;

  void _parseShifts(Iterable<Shift> shifts) {
    shifts.forEach((shift) {
      this.unavailabilityStartDates.add(shift.unavailabilityStartDate);
      this.unavailabilityEndDates.add(shift.unavailabilityEndDate);
      this.boardingDates.add(shift.boardingDate);
      this.leavingDates.add(shift.leavingDate);

      Duration diff = shift.leavingDate.difference(shift.boardingDate);
      while (diff.inDays > 0) {
        unavailableDates.add(shift.leavingDate.subtract(diff));
        diff = new Duration(days: diff.inDays - 1);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      fit: StackFit.expand,
      children: [
        CustomScrollView(
          shrinkWrap: true,
          slivers: [
            SliverAppBar(
              automaticallyImplyLeading: false,
              title: Text('Calendar'.i18n),
              pinned: true,
              actions: widget.defaultActions,
            ),
            Consumer<ShiftListModel>(
                builder: (context, model, child) {
                  if (model.isLoading) {
                    return SliverFillRemaining(
                        child: Center(
                      child: CircularProgressIndicator(),
                    ));
                  } else {
                    var shifts = model.shifts;
                    _parseShifts(shifts);
                    return SliverList(
                        delegate: SliverChildListDelegate.fixed([
                      TableCalendar(
                          locale: I18n.locale.toLanguageTag(),
                          onDaySelected: (selected, focused) {
                            Navigator.of(context).push(MaterialPageRoute(
                                fullscreenDialog: true,
                                builder: (context) => DayView(selected)));
                          },
                          headerStyle: HeaderStyle(
                            formatButtonVisible: false,
                          ),
                          calendarBuilders: CalendarBuilders(
                            prioritizedBuilder:
                                (context, DateTime day, focusedDay) {
                              Color? color;
                              if (boardingDates.contains(day)) {
                                color = boardingColor;
                              } else if (unavailabilityStartDates
                                  .contains(day)) {
                                color = unStartColor;
                              } else if (unavailableDates.contains(day)) {
                                color = unavailableColor;
                              } else if (leavingDates.contains(day)) {
                                color = leavingColor;
                              } else if (unavailabilityEndDates.contains(day)) {
                                color = unEndColor;
                              }
                              return color == null
                                  ? null
                                  : Container(
                                      alignment: Alignment.center,
                                      decoration: BoxDecoration(color: color),
                                      child: Text(day.day.toString()),
                                    );
                            },
                          ),
                          focusedDay: DateTime.now(),
                          firstDay:
                              DateTime.now().subtract(Duration(days: 2000)),
                          lastDay: DateTime.now().add(Duration(days: 2000))),
                      child!
                    ]));
                  }
                },
                child: SubtitleTable(<Color, String>{
                  unStartColor: 'Start of unavailability'.i18n,
                  boardingColor: 'Boarding day'.i18n,
                  leavingColor: 'Leaving day'.i18n,
                  unEndColor: 'End of unavailability'.i18n
                })),
          ],
        ),
        Positioned(
          bottom: 20,
          right: 20,
          child: FloatingActionButton(
            tooltip: "Shift".i18n,
            child: Icon(Icons.add),
            onPressed: () {
              Navigator.push(
                  context,
                  MaterialPageRoute(
                      fullscreenDialog: true,
                      builder: (context) => ShiftAddView()));
            },
          ),
        )
      ],
    );
  }
}

class SubtitleTable extends StatelessWidget {
  const SubtitleTable(this.entries);
  final Map<Color, String> entries;

  List<TableRow> getRows() {
    List<TableRow> result = [];
    entries.forEach((key, value) {
      result.add(TableRow(
        children: [
          Container(
            color: key,
            height: 30,
          ),
          Padding(
            padding: EdgeInsets.only(left: 10),
            child: Text(value),
          )
        ],
      ));
    });
    return result;
  }

  @override
  Widget build(BuildContext context) {
    return Table(
      defaultVerticalAlignment: TableCellVerticalAlignment.middle,
      columnWidths: {0: FixedColumnWidth(30), 1: FlexColumnWidth()},
      children: getRows(),
    );
  }
}

int _getHashCode(DateTime key) {
  int result = 17;
  result = 31 * result + key.day;
  result = 31 * result + key.month;
  result = 31 * result + key.year;
  return result;
}
