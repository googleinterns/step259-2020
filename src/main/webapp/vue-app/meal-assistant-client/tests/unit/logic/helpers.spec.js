import { capitalizeItems } from "@/logic/helpers.js";

describe("capitalizeItems", () => {
  it("handles one word", () => {
    expect(capitalizeItems("word")).toMatch("Word");
  });

  it("handles one sentence", () => {
    expect(capitalizeItems("multi word sentence")).toMatch(
      "Multi Word Sentence"
    );
  });
});
