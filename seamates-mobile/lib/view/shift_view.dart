import 'package:flutter/material.dart';
import 'package:i18n_extension/i18n_widget.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/data/sync_status.dart';
import 'package:sea_mates/model/shift_list_model.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/view/shift_add_view.dart';

class ShiftView extends StatefulWidget {
  const ShiftView(this.defaultActions);
  final List<Widget> defaultActions;
  @override
  State<StatefulWidget> createState() => _ShiftViewState();
}

class _ShiftViewState extends State<ShiftView> {
  final DateFormat dateFormat = DateFormat.yMd(I18n.localeStr);

  void _toggleSelectedLong(int shiftId) {
    var model = Provider.of<ShiftListModel>(context, listen: false);
    if (model.selectedIds.contains(shiftId)) {
      model.unselectId(shiftId);
    } else {
      model.selectId(shiftId);
    }
  }

  void _toggleSelectedTap(int shiftId) {
    var model = Provider.of<ShiftListModel>(context, listen: false);
    if (model.selectedIds.isEmpty) {
      // Reserved for future actions
      return;
    }

    if (model.selectedIds.contains(shiftId)) {
      model.unselectId(shiftId);
    } else {
      model.selectId(shiftId);
    }
  }

  Future<void> _deleteShifts() async {
    var message = await Provider.of<ShiftListModel>(context, listen: false)
        .removeSelected();

    if (message != null) {
      _showSnackbar(context, message);
    } else {
      _showSnackbar(context, 'Deleted!'.i18n);
    }
  }

  Future<void> _refresh() async {
    var message =
        await Provider.of<ShiftListModel>(context, listen: false).syncShifts();
    if (message != null) {
      _showSnackbar(context, message);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        RefreshIndicator(
          onRefresh: () async => _refresh(),
          child: CustomScrollView(
            slivers: [
              Consumer<ShiftListModel>(
                builder: (context, model, child) {
                  var selectedSize = model.selectedIds.length;
                  Widget? leading;
                  List<Widget> actions = widget.defaultActions;
                  String title = 'Shifts'.i18n;

                  if (selectedSize != 0) {
                    leading = IconButton(
                      icon: Icon(Icons.arrow_back),
                      onPressed: () {
                        model.resetSelection();
                      },
                    );
                    actions = [
                      IconButton(
                          icon: Icon(Icons.delete), onPressed: _deleteShifts)
                    ];
                    title = selectedSize.toString();
                  }

                  return SliverAppBar(
                    automaticallyImplyLeading: false,
                    title: Text(title),
                    leading: leading,
                    actions: actions,
                    pinned: true,
                  );
                },
              ),
              Consumer<ShiftListModel>(builder: (context, model, child) {
                if (model.isLoading) {
                  return SliverFillRemaining(
                      child: Center(
                    child: CircularProgressIndicator(),
                  ));
                } else {
                  var shifts = model.shifts;
                  var selectedIds = model.selectedIds;
                  return SliverPadding(
                    padding: EdgeInsets.all(0),
                    sliver: SliverList(
                      delegate:
                          SliverChildBuilderDelegate((context, int index) {
                        var shift = shifts[index];
                        var id = shift.id!;
                        return ListTile(
                            title: Text(
                              dateFormat.format(shift.unavailabilityStartDate) +
                                  " ~ " +
                                  dateFormat
                                      .format(shift.unavailabilityEndDate),
                              textScaleFactor: 1.1,
                            ),
                            trailing:
                                shifts[index].syncStatus == SyncStatus.UNSYNC
                                    ? Icon(Icons.hourglass_top)
                                    : Icon(Icons.check),
                            selected: selectedIds.contains(id),
                            selectedTileColor: Color.fromRGBO(200, 200, 200, 1),
                            onLongPress: () {
                              _toggleSelectedLong(id);
                            },
                            onTap: () => _toggleSelectedTap(id));
                      }, childCount: shifts.length),
                    ),
                  );
                }
              }),
            ],
          ),
        ),
        Positioned(
          bottom: 20,
          right: 20,
          child: FloatingActionButton(
            child: Icon(Icons.add),
            onPressed: () {
              // call shift dialog
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

/// timeout in seconds
void _showSnackbar(BuildContext context, String message, [int timeout = 2]) {
  ScaffoldMessenger.of(context).showSnackBar(SnackBar(
    content: Text(message),
    duration: Duration(seconds: timeout),
  ));
}
