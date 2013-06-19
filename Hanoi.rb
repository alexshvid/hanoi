MAX_STEPS = 8

nk = gets.split(' ').map { |x| x.to_i }

iState = gets.split(' ').map { |x| x.to_i }
fState = gets.split(' ').map { |x| x.to_i }

time = Time.now  
  
iState = iState.map { |x| x-1 }
fState = fState.map { |x| x-1 }

n = nk[0]
k = nk[1]

class Move
  def initialize(from, to)
    @from=from
    @to=to
  end

  def from()
    @from
  end

  def to()
    @to
  end

  def to_s()
    '' + @from.to_s + ' ' + @to.to_s
  end

end

def genAtomicMoves(k)
  rez = []
  (0..k-1).each do |x|
    (0..k-1).each do |y|
      if x != y
        rez << Move.new(x,y)
      end
    end
  end
  return rez
end

class State
  def initialize(st)
    @state=st.dup
  end

  def to_s
    @state.to_s
  end

  def eql?(other)
    @state.eql?(other.state)
  end

  def state
    @state
  end

  def set(i, v)
    @state[i] = v
    self
  end

  def hasSmallerDisks(radius, peg)
    (0..radius-1).each do |i|
      if @state[i] == peg
        return true
      end
    end
    false
  end

  def apply(move)
    (0..@state.size-1).each do |i|
      if @state[i] == move.from and not hasSmallerDisks(i, move.to)
        return State.new(@state).set(i, move.to)
      end
    end
    nil
  end

end

class Step
  def initialize(state, move, prev = nil)
    @state = state
    @move = move
    if prev != nil
      @steps = prev.steps + 1
    else
      @steps = 0
    end
    @prev = prev
  end

  def state
    @state
  end
  
  def prev
    @prev
  end
  
  def steps
    @steps
  end
  
  def move
    @move
  end
  
  def hasAlready?(newState)
    step = self;
    while step != nil do
      if newState.eql?(step.state)
        return true
      end
      step = step.prev;
    end
    return false
  end

  def next(move)
    newState = @state.apply(move)
    if newState != nil
      if self.hasAlready?(newState)
        return nil
      end
      return Step.new(newState, move, self)
    end
    return nil
  end

  def moves()
    moves = []
    step = self
    while step != nil and step.move != nil do
      moves << step.move
      step = step.prev
    end
    moves.reverse()
    moves
  end
end

def solve(step, solutions = [])
  $atomicMoves.each do |move|
    nextStep = step.next(move)
    if nextStep != nil
      if nextStep.state.eql?($finalState)
        solutions << nextStep
      elsif nextStep.steps <= MAX_STEPS
        solve(nextStep, solutions)
      end
    end
  end
  solutions
end

$atomicMoves = genAtomicMoves(k)
$initState = State.new(iState)
$finalState = State.new(fState)

initStep = Step.new($initState, nil)
          
solutions = solve(initStep)

if solutions.size > 0
  sols = solutions.sort_by { |s| [s.steps] }
  minSteps = sols[0].steps
  solutions = []
  i = 0  
  while i != sols.size do
    sol = sols[i]
    if sol.steps > minSteps
      break
    end
    solutions << sol
    i = i + 1
  end
end
  

time = Time.now - time

puts(time)

solutions.each do |sol|
  puts(sol.steps)
  sol.moves.each do |move|
    puts(move)
  end
end            
