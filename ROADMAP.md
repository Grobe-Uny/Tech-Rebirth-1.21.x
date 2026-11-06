# Tech Rebirth Roadmap

A living list of planned improvements and ideas. Items are grouped by target version based on scope and dependencies; priorities can change as development progresses.

## 0.0.6
_No items assigned yet._

## 0.0.7

### Energy system refactor
- [ ] Centralize energy behavior into a reusable configuration/API so machines and energy blocks do not need scattered, repeated checks.
- [ ] Keep these capabilities independent and explicit:
  - Receive energy
  - Extract/consume energy
  - Generate energy
  - Store energy
- [ ] Define sensible behavior per block type:
  - Generators: generate and store energy, optionally emit it; do not receive or consume energy unless explicitly configured.
  - Machines: receive and consume energy; do not generate it.
  - Energy banks: receive, store, and emit energy; do not generate or consume it.
- [ ] Make the rules easy to read, configure, and extend when new energy-based blocks are added.

## 0.1.0
_No items assigned yet._

## Backlog
_Unsorted ideas will be placed here until a suitable release target is clear._

## Completed
_Items move here when implemented and verified in-game._
