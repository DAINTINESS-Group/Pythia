package gr.uoi.cs.pythia.labeling;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RuleSet {
  private String newColumnName;
  private List<Rule> rules;
}
