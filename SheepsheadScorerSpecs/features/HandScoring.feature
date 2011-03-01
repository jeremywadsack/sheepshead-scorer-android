Feature: Hand Scoring
    As a sheepshead player
    I want to be able to score a hand
    So that I don't have to do math and drink beer at once

Scenario: Picker has a partner
    Given that the picker has a partner
    And that the picker's team has more than at least 0 and at most 30 points
    And that the picker's team took one or more tricks
    When the hand is scored
    Then the picker should lose 8 points
    And the partner should lose 4 points
    And the opponents should all gain 4 points

    Given that the picker has a partner
    And that the picker's team has more than at least 31 and at most 59 points
    When the hand is scored
    Then the picker should lose 4 points
    And the partner should lose 2 points
    And the opponents should all gain 2 points

    Given that the picker has a partner
    And that the picker's team has exactly 60 points
    When the hand is scored
    Then the picker should lose 4 points
    And the partner should lose 2 points
    And the opponents should all gain 2 points
    And the next hand is doubled

    Given that the picker has a partner
    And that the picker's team has more than at least 61 and at most 90 points
    When the hand is scored
    Then the picker should gain 2 points
    And the partner should gain 1 point
    And the opponents should all lose 1 point

    Given that the picker has a partner
    And that the picker's team has more than at least 91 and at most 120 points
    And that the opponents team took one or more tricks
    When the hand is scored
    Then the picker should gain 4 points
    And the partner should gain 2 points
    And the opponents should all lose 2 point

    Given that the picker has a partner
    And that the opponent's team took no tricks
    When the hand is scored
    Then the picker should gain 8 points
    And the partner should gain 4 point
    And the opponents should all lose 4 point


Scenario: Picker goes a lone
    Given that the picker has no partner
    And that the picker's team has more than at least 0 and at most 30 points
    And that the picker's team took one or more tricks
    When the hand is scored
    Then the picker should lose 16 points
    And the opponents should all gain 4 points

    Given that the picker has no partner
    And that the picker's team has more than at least 31 and at most 59 points
    When the hand is scored
    Then the picker should lose 8 points
    And the opponents should all gain 2 points

    Given that the picker has no partner
    And that the picker's team has exactly 60 points
    When the hand is scored
    Then the picker should lose 8 points
    And the opponents should all gain 2 points
    And the next hand is doubled

    Given that the picker has no partner
    And that the picker's team has more than at least 61 and at most 90 points
    When the hand is scored
    Then the picker should gain 4 points
    And the opponents should all lose 1 point

    Given that the picker has no partner
    And that the picker's team has more than at least 91 and at most 120 points
    And that the opponents team took one or more tricks
    When the hand is scored
    Then the picker should gain 8 points
    And the opponents should all lose 2 point

    Given that the picker has no partner
    And that the opponent's team took no tricks
    When the hand is scored
    Then the picker should gain 16 points
    And the opponents should all lose 4 point

Scenario: Hand is doubled from previous hand


Scenario: A player cracks on the hand


Scenario: The last hand in the game

